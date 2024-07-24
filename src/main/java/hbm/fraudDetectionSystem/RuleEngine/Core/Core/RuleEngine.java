package hbm.fraudDetectionSystem.RuleEngine.Core.Core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hbm.fraudDetectionSystem.ReactionEngine.Core.ReactionEngine;
import hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.ExReaction.ReactionContainer;
import hbm.fraudDetectionSystem.ReactionEngine.Enum.BindingTypeEnum;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule.Rule;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule.RuleRepository;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleGroup.RuleGroup;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleGroup.RuleGroupRepository;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleTriggered.RuleTriggered;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static hbm.fraudDetectionSystem.ReactionEngine.Enum.ReactionEnum.SET_RESPCODE;

@Slf4j
@Service
public class RuleEngine {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final RuleRepository ruleRepository;
    protected final RuleGroupRepository ruleGroupRepository;
    protected final ReactionEngine reactionEngine;

    @Autowired
    RuleEngine(RuleRepository ruleRepository, RuleGroupRepository ruleGroupRepository, ReactionEngine reactionEngine) {
        this.ruleRepository = ruleRepository;
        this.ruleGroupRepository = ruleGroupRepository;
        this.reactionEngine = reactionEngine;
    }

    public String run(Map<String, String> transactionData) {
        LOGGER.info(
                String.format(
                        "Receive data from transaction engine, detail: [UTRNNO: %s], [PID: %s]",
                        transactionData.get("utrnno"), transactionData.get("pid")
                )
        );

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        Instant currentDate = Timestamp.valueOf(transactionData.get("sysdate")).toInstant();
        List<RuleTriggered> triggeredRules = new ArrayList<>();
        List<ReactionContainer> reactions = new ArrayList<>();
        Long utrnno = Long.valueOf(transactionData.get("utrnno"));

        for (RuleGroup ruleGroup : ruleGroupRepository.findAllByIsActiveTrueOrderByPriorityAsc()) {
            List<ReactionContainer> tempReactions = new ArrayList<>();
            AtomicInteger riskValue = new AtomicInteger();

            for (Rule rule : ruleRepository.findAllByRuleGroupIdAndIsActiveTrueOrderByPriority(ruleGroup.getId())) {

                Instant dateFrom = rule.getDateFrom().toInstant();
                Instant dateTo = rule.getDateTo().toInstant();

                //Core engine of rule
                InferenceEngine inferenceEngine = new InferenceEngine();

                if (isRuleDateIsNotExpired(currentDate, dateFrom, dateTo)) {
                    try {
                        LOGGER.info(
                                String.format(
                                        "START EVALUATE RULE ID: [%s] NAME: [%s] PRIORITY: [%s]",
                                        rule.getRuleId(), rule.getRuleName().toUpperCase(), rule.getPriority()
                                )
                        );

                        //This is main method of rule
                        inferenceEngine.run(rule, transactionData);

                        if (inferenceEngine.isRuleTriggered()) {
                            LOGGER.info(
                                    String.format(
                                            "FINISH EVALUATE RULE ID: [%s] NAME: [%s] PRIORITY: [%s] RESULT: [%s]",
                                              rule.getRuleId(), rule.getRuleName().toUpperCase(), rule.getPriority(), true
                                    )
                            );

                            /*
                                This will convert the triggered rule to JSON format and map it to RuleTriggered Class
                                and the last it will be added to container
                             */
                            triggeredRules.add(
                                    RuleTriggered.builder()
                                            .utrnno(utrnno)
                                            .detailObj(new Gson().toJson(rule))
                                            .ruleId(rule.getRuleId())
                                            .build()
                            );
                            this.countRiskValue(riskValue, rule.getRiskValue());

                            /*
                                This method will filter the reaction that was executed with type:
                                - SET_RESPCODE
                                - CREATE_ALERT
                                so those type should be executed 1 time, not multiple time
                             */
                            this.reactionEngine
                                    .collect(rule.getRuleId(), BindingTypeEnum.RULE.getName(), "", transactionData)
                                    .forEach(
                                            v -> {
                                                for (ReactionContainer reactionContainer : tempReactions) {
                                                    if (reactionContainer.getReactionEnum() == v.getReactionEnum()) {
                                                        return;
                                                    }
                                                }
                                                tempReactions.add(v);
                                            }
                                    );
                        } else {
                            LOGGER.info(
                                    String.format(
                                            "FINISH EVALUATE RULE ID: [%s] NAME: [%s] PRIORITY: [%s] RESULT: [%s]",
                                            rule.getRuleId(), rule.getRuleName().toUpperCase(), rule.getPriority(), false
                                    )
                            );
                        }
                    } catch (Exception e) {
                        LOGGER.error(
                                String.format(
                                        "FINISH EVALUATE RULE ID: [%s] NAME: [%s] PRIORITY: [%s] RESULT: [ERROR], DETAIL: [%s]",
                                        rule.getRuleId(), rule.getRuleName().toUpperCase(), rule.getPriority(), e.getMessage()
                                )
                        );
                        LOGGER.error("", e);
                    }
                }
            }

            /*
                Convert the triggered rule container to string format for storing to transaction data container
             */
            transactionData.put("triggeredRules", new Gson().toJson(triggeredRules));

            if (isGreyThresholdTriggered(ruleGroup, riskValue.get())) {
                /*
                    This method will filter the reaction that was executed with type:
                    - SET_RESPCODE
                    - CREATE_ALERT
                    so those type should be executed 1 time, not multiple time
                 */
                this.reactionEngine
                        .collect(ruleGroup.getId(), BindingTypeEnum.RULE_GROUP.getName(), "GREY", transactionData)
                        .forEach(
                                v -> {
                                    for (ReactionContainer reactionContainer : tempReactions) {
                                        if (reactionContainer.getReactionEnum() == v.getReactionEnum()) {
                                            return;
                                        }
                                    }
                                    tempReactions.add(v);
                                }
                        );

//                Map<String, Object> reactions = this.reactionEngine
//                        .collect(ruleGroup.getId(), BindingTypeEnum.RULE_GROUP.getName(), "GREY", transactionData);
//
//                reactions.forEach(dictionaryData::putIfAbsent);

//                if (dictionaryData.containsKey(SET_RESPCODE.getName())){
//                    dictionaryData.put(SET_RESPCODE.getName(), dictionaryData.get(SET_RESPCODE.getName()));
//                }
            }

            if (isBlackThresholdTriggered(ruleGroup, riskValue.get())) {
                /*
                    This method will filter the reaction that was executed with type:
                    - SET_RESPCODE
                    - CREATE_ALERT
                    so those type should be executed 1 time, not multiple time
                 */
                this.reactionEngine
                        .collect(ruleGroup.getId(), BindingTypeEnum.RULE_GROUP.getName(), "BLACK", transactionData)
                        .forEach(
                                v -> {
                                    for (ReactionContainer reactionContainer : tempReactions) {
                                        if (reactionContainer.getReactionEnum() == v.getReactionEnum()) {
                                            return;
                                        }
                                    }
                                    tempReactions.add(v);
                                }
                        );

//                Map<String, Object> reactions = this.reactionEngine
//                        .collect(ruleGroup.getId(), BindingTypeEnum.RULE_GROUP.getName(), "BLACK", transactionData);
//
//                reactions.forEach(dictionaryData::putIfAbsent);

//                if (dictionaryData.containsKey(SET_RESPCODE.getName())){
//                    dictionaryData.put(SET_RESPCODE.getName(), dictionaryData.get(SET_RESPCODE.getName()));
//                }
            }

            for (ReactionContainer tempReaction : tempReactions) {
                if (reactions.stream().noneMatch(v -> v.getReactionEnum() == tempReaction.getReactionEnum() && v.getReactionEnum() == SET_RESPCODE)) {
                    reactions.add(tempReaction);
                } else if (tempReaction.getReactionEnum() != SET_RESPCODE) reactions.add(tempReaction);
            }

            //Reset reaction when processing other rule group
//            tempReactions.removeIf(v -> v.getReactionEnum() != SET_RESPCODE);

//            dictionaryData.forEach((k, v) -> {
//                if (!Objects.equals(k, SET_RESPCODE.getName()) || !Objects.equals(k, "isAlerted")) {
//                    dictionaryData.remove(k);
//                }
//            });
        }

        //This line will run the reaction (combined)
        this.reactionEngine.run(reactions);

        /*
            Convert the reaction to string format for storing to transaction data container
         */
        transactionData.put("exReactions", gson.toJson(reactions));

        return this.validateResponseCode(reactions);
    }


    protected boolean isRuleDateIsNotExpired(Instant currentDate, Instant dateFrom, Instant dateTo) {
        return currentDate.isAfter(dateFrom) && currentDate.isBefore(dateTo);
    }

    protected void countRiskValue(AtomicInteger riskValue, int tempRiskValue) {
        riskValue.addAndGet(tempRiskValue);
    }

    protected boolean isGreyThresholdTriggered(RuleGroup ruleGroup, int riskValue) {
        return riskValue >= ruleGroup.getThreshouldGrey() && riskValue < ruleGroup.getThreshouldBlack();
    }

    protected boolean isBlackThresholdTriggered(RuleGroup ruleGroup, int riskValue) {
        return riskValue >= ruleGroup.getThreshouldBlack();
    }

    protected String validateResponseCode(List<ReactionContainer> dictionaryData) {
        List<ReactionContainer> collection = dictionaryData.stream().filter(v -> v.getReactionEnum() == SET_RESPCODE).collect(Collectors.toList());
        ReactionContainer respCode = collection.size() > 0 ? collection.get(0) : null;
        return respCode != null ?
                respCode.getReactionValue() : null;
    }

//    protected String validateResponseCode(Map<String, Object> dictionaryData) {
//        Object respCode = dictionaryData.get(SET_RESPCODE.getName());
//        return respCode != null ?
//                respCode.toString() : null;
//    }
}
