package hbm.fraudDetectionSystem.ReactionEngine.Core;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import hbm.fraudDetectionSystem.ReactionEngine.Core.ReactionType.*;
import hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.ExReaction.ReactionContainer;
import hbm.fraudDetectionSystem.ReactionEngine.Enum.ReactionEnum;
import hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.FraudReaction.FraudReaction;
import hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.FraudReaction.FraudReactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static hbm.fraudDetectionSystem.ReactionEngine.Enum.ReactionEnum.*;

@Service
@Slf4j
public class ReactionEngine {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected final ExecutorService reactionThreadPool;
    protected final FraudReactionRepository fraudReactionRepository;

    public ReactionEngine(FraudReactionRepository fraudReactionRepository) {
        this.fraudReactionRepository = fraudReactionRepository;
        this.reactionThreadPool = Executors.newFixedThreadPool(10);
    }

    public List<ReactionContainer> collect(long bindingId, String bindingType, String zone, Map<String, String> transactionData) {
        ObjectMapper om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);

        String reqId = MDC.get("req_id");
        List<ReactionContainer> dictionaryData = new LinkedList<>();

        Long utrnno = Long.valueOf(transactionData.get("utrnno"));

        LOGGER.info(
                String.format(
                        "Receive data from rule engine, detail: [UTRNNO: %s, BINDING ID: %s, BINDING TYPE: %s, ZONE: %s], [PID: %s]",
                        transactionData.get("utrnno"), bindingId, bindingType, zone, transactionData.get("pid")
                )
        );

        LOGGER.info(
                String.format(
                        "Start checking reaction for binding id: [%s], binding type: [%s]",
                        bindingId, bindingType
                )
        );

        for (FraudReaction reaction : this.fetchReaction(bindingId, bindingType, zone)) {
            switch (ReactionEnum.fromName(reaction.getAction())) {
                case SET_RESPCODE:
                    dictionaryData.add(
                            ReactionContainer.builder()
                                    .reactionId(reaction.getId())
                                    .utrnno(utrnno)
                                    .reactionEnum(SET_RESPCODE)
                                    .bindingId(String.valueOf(bindingId))
                                    .bindingType(bindingType)
                                    .zone(zone)
                                    .reactionValue(new SetResponseCode().run(reaction))
                                    .build()
                    );
//                    dictionaryData.put(SET_RESPCODE.getName(), new SetResponseCode().run(reaction));
                    LOGGER.info(
                            String.format(
                                    "Got reaction: \n\tId: [%s] \n\tBinding type: [%s] \n\tZone: [%s] \n\tType: [SET_RESPCODE] \n\tValue: [%s]",
                                    reaction.getId(), reaction.getBindingType(), reaction.getZone(), reaction.getActionValue()
                            )
                    );
                    break;

                case CREATE_ALERT:
//                    this.reactionThreadPool.submit(new CreateAlert(reqId, reaction, transactionData));
//                    dictionaryData.put(CREATE_ALERT.getName(), "EXECUTED");
//                    dictionaryData.put("isAlerted", true);

                    //Add this line for limit the reaction should be run 1 time
//                    dictionaryData.put(CREATE_ALERT.getName(), new CreateAlert(reqId, reaction, transactionData));
                    transactionData.put("isAlerted", "true");

                    dictionaryData.add(
                            ReactionContainer.builder()
                                    .reactionId(reaction.getId())
                                    .utrnno(utrnno)
                                    .reactionEnum(CREATE_ALERT)
                                    .bindingId(String.valueOf(bindingId))
                                    .bindingType(bindingType)
                                    .zone(zone)
                                    .reaction(new CreateAlert(reqId, reaction, transactionData))
                                    .build()
                    );

                    LOGGER.info(
                            String.format(
                                    "Got reaction: \n\tId: [%s] \n\tBinding type: [%s] \n\tZone: [%s] \n\tType: [CREATE_ALERT] \n\tValue: [%s]",
                                    reaction.getId(), reaction.getBindingType(), reaction.getZone(), reaction.getActionValue()
                            )
                    );
                    break;

                case SMS_NOTIFICATION:
                    //TODO: Not ready yet, need more analyze
                    break;

                case EMAIL_NOTIFICATION:
//                    this.reactionThreadPool.submit(new EmailNotification(reqId, reaction, transactionData));
//                    dictionaryData.put(EMAIL_NOTIFICATION.getName(), new EmailNotification(reqId, reaction, transactionData));
                    dictionaryData.add(
                            ReactionContainer.builder()
                                    .reactionId(reaction.getId())
                                    .utrnno(utrnno)
                                    .reactionEnum(EMAIL_NOTIFICATION)
                                    .bindingId(String.valueOf(bindingId))
                                    .bindingType(bindingType)
                                    .zone(zone)
                                    .reaction(new EmailNotification(reqId, reaction, transactionData))
                                    .reactionValue(reaction.getActionValue())
                                    .build()
                    );

                    try {
                        LOGGER.info(
                                String.format(
                                        "Got reaction: \n\tId: [%s] \n\tBinding type: [%s] \n\tZone: [%s] \n\tType: [EMAIL_NOTIFICATION] \n\tValue: [\n%s\n\t]",
                                        reaction.getId(), reaction.getBindingType(), reaction.getZone(), om.writeValueAsString(om.readTree(reaction.getActionValue()))
                                )
                        );
                    } catch (JsonProcessingException e) {
                        LOGGER.error("", e);
                    }
                    break;

                case ATTR_WHITE_LIST:
//                    this.reactionThreadPool.submit(new PutToWhiteList(reqId, reaction, transactionData));
//                    dictionaryData.put(ATTR_WHITE_LIST.getName(), new PutToWhiteList(reqId, reaction, transactionData));

                    dictionaryData.add(
                            ReactionContainer.builder()
                                    .reactionId(reaction.getId())
                                    .utrnno(utrnno)
                                    .reactionEnum(ATTR_WHITE_LIST)
                                    .bindingId(String.valueOf(bindingId))
                                    .bindingType(bindingType)
                                    .zone(zone)
                                    .reaction(new PutToWhiteList(reqId, reaction, transactionData))
                                    .reactionValue(reaction.getActionValue())
                                    .build()
                    );

                    try {
                        LOGGER.info(
                                String.format(
                                        "Got reaction: \n\tId: [%s] \n\tBinding type: [%s] \n\tZone: [%s] \n\tType: [ATTR_WHITE_LIST] \n\tValue: [\n%s\n\t]",
                                        reaction.getId(), reaction.getBindingType(), reaction.getZone(), om.writeValueAsString(om.readTree(reaction.getActionValue()))
                                )
                        );
                    } catch (JsonProcessingException e) {
                        LOGGER.error("", e);
                    }
                    break;

                case ATTR_BLACK_LIST:
//                    this.reactionThreadPool.submit(new PutToBlackList(reqId, reaction, transactionData));
//                    dictionaryData.put(ATTR_BLACK_LIST.getName(), new PutToBlackList(reqId, reaction, transactionData));

                    dictionaryData.add(
                            ReactionContainer.builder()
                                    .reactionId(reaction.getId())
                                    .utrnno(utrnno)
                                    .reactionEnum(ATTR_BLACK_LIST)
                                    .bindingId(String.valueOf(bindingId))
                                    .bindingType(bindingType)
                                    .zone(zone)
                                    .reaction(new PutToBlackList(reqId, reaction, transactionData))
                                    .reactionValue(reaction.getActionValue())
                                    .build()
                    );

                    try {
                        LOGGER.info(
                                String.format(
                                        "Got reaction: \n\tId: [%s] \n\tBinding type: [%s] \n\tZone: [%s] \n\tType: [ATTR_BLACK_LIST] \n\tValue: [\n%s\n\t]",
                                        reaction.getId(), reaction.getBindingType(), reaction.getZone(), om.writeValueAsString(om.readTree(reaction.getActionValue()))
                                )
                        );
                    } catch (JsonProcessingException e) {
                        LOGGER.error("", e);
                    }
                    break;

                case ATTR_FRAUD_LIST:
//                    this.reactionThreadPool.submit(new PutToFraudList(reqId, reaction, transactionData));
//                    dictionaryData.put(ATTR_FRAUD_LIST.getName(), new PutToFraudList(reqId, reaction, transactionData));

                    dictionaryData.add(
                            ReactionContainer.builder()
                                    .reactionId(reaction.getId())
                                    .utrnno(utrnno)
                                    .reactionEnum(ATTR_FRAUD_LIST)
                                    .bindingId(String.valueOf(bindingId))
                                    .bindingType(bindingType)
                                    .zone(zone)
                                    .reaction(new PutToFraudList(reqId, reaction, transactionData))
                                    .reactionValue(reaction.getActionValue())
                                    .build()
                    );

                    try {
                        LOGGER.info(
                                String.format(
                                        "Got reaction: \n\tId: [%s] \n\tBinding type: [%s] \n\tZone: [%s] \n\tType: [ATTR_FRAUD_LIST] \n\tValue: [\n%s\n\t]",
                                        reaction.getId(), reaction.getBindingType(), reaction.getZone(), om.writeValueAsString(om.readTree(reaction.getActionValue()))
                                )
                        );
                    } catch (JsonProcessingException e) {
                        LOGGER.error("", e);
                    }
                    break;

                default:
                    break;
            }
        }

        LOGGER.info("Clear duplicated reaction...");

        LOGGER.info(
                String.format(
                        "Finish checking reaction for binding id: [%s], binding type: [%s]",
                        bindingId, bindingType
                )
        );

        return dictionaryData;
    }

    public void run(List<ReactionContainer> reactionList) {
        ObjectMapper om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);

        LOGGER.info(
                String.format(
                        "Start executing reactions with total: [%s]",
                        reactionList.size()
                )
        );

        reactionList.forEach(v -> {
            if (v.getReaction() != null) {

                if (this.isValidJSON(v.getReactionValue())) {
                    try {
                        LOGGER.info(
                                String.format(
                                        "execute reaction: \n\tId: [%s] \n\tBinding type: [%s] \n\tZone: [%s] \n\tType: [%s] \n\tValue: [\n%s\n\t]",
                                        v.getId(), v.getBindingType(), v.getZone(), v.getReactionEnum().getName(), om.writeValueAsString(om.readTree(v.getReactionValue()))
                                )
                        );
                    } catch (JsonProcessingException e) {
                        LOGGER.error("", e);
                    }
                } else {
                    LOGGER.info(
                            String.format(
                                    "execute reaction: \n\tId: [%s] \n\tBinding type: [%s] \n\tZone: [%s] \n\tType: [%s] \n\tValue: [%s]",
                                    v.getId(), v.getBindingType(), v.getZone(), v.getReactionEnum().getName(), v.getReactionValue()
                            )
                    );
                }

                this.reactionThreadPool.submit(v.getReaction());
            }
        });

//        reactionList.forEach((k,v) -> {
//            if (v instanceof Runnable) {
//                LOGGER.info(
//                        String.format(
//                                "execute reaction: [%s]",
//                                k
//                        )
//                );
//                this.reactionThreadPool.submit((Runnable) v);
//
////                triggeredReactions.add(
////                        ReactionTriggered.builder()
////                                .utrnno(utrnno)
////                                .detailObj(new Gson())
////                                .build()
////                )
//            }
//        });
    }

    protected List<FraudReaction> fetchReaction(long bindingId, String bindingType, String zone) {
        return this.fraudReactionRepository.findReactionByBindingTypeAndBindingIdAndZone(bindingType, bindingId, zone);
    }

    protected boolean isValidJSON(String json) {
        if (json != null) {
            final String jsonPattern = "^(\\{.*\\}|\\[.*\\])$";

            Pattern pattern = Pattern.compile(jsonPattern);
            Matcher matcher = pattern.matcher(json);

            return matcher.matches();
        }

        return false;
    }

//    public Map<String, Object> run(long bindingId, String bindingType, String zone, Map<String, String> transactionData) {
//        String reqId = MDC.get("req_id");
//        Map<String, Object> dictionaryData = new LinkedHashMap<>();
//
//        log.info(
//                String.format(
//                        "Receive data from rule engine, detail: [UTRNNO: %s, BINDING ID: %s, BINDING TYPE: %s, ZONE: %s], [PID: %s]",
//                        transactionData.get("utrnno"), bindingId, bindingType, zone, transactionData.get("pid")
//                )
//        );
//
//        for (FraudReaction reaction : this.fetchReaction(bindingId, bindingType, zone)) {
//            log.info(
//                    String.format(
//                            "Start executing reaction id [%s] action[%s] action value: [%s]",
//                            reaction.getId(), reaction.getAction(), reaction.getActionValue()
//                    )
//            );
//
//            switch (ReactionEnum.fromName(reaction.getAction())) {
//                case SET_RESPCODE:
//                    dictionaryData.put(SET_RESPCODE.getName(), new SetResponseCode().run(reaction));
//                    break;
//
//                case CREATE_ALERT:
////                    this.reactionThreadPool.submit(new CreateAlert(reqId, reaction, transactionData));
////                    dictionaryData.put(CREATE_ALERT.getName(), "EXECUTED");
////                    dictionaryData.put("isAlerted", true);
//
//                    //Add this line for limit the reaction should be run 1 time
//                    dictionaryData.put(CREATE_ALERT.getName(), new CreateAlert(reqId, reaction, transactionData));
//                    dictionaryData.put("isAlerted", true);
//                    break;
//
//                case SMS_NOTIFICATION:
//                    //TODO: Not ready yet, need more analyze
//                    break;
//
//                case EMAIL_NOTIFICATION:
////                    this.reactionThreadPool.submit(new EmailNotification(reqId, reaction, transactionData));
//                    dictionaryData.put(EMAIL_NOTIFICATION.getName(), new EmailNotification(reqId, reaction, transactionData));
//                    break;
//
//                case ATTR_WHITE_LIST:
////                    this.reactionThreadPool.submit(new PutToWhiteList(reqId, reaction, transactionData));
//                    dictionaryData.put(ATTR_WHITE_LIST.getName(), new PutToWhiteList(reqId, reaction, transactionData));
//                    break;
//
//                case ATTR_BLACK_LIST:
////                    this.reactionThreadPool.submit(new PutToBlackList(reqId, reaction, transactionData));
//                    dictionaryData.put(ATTR_BLACK_LIST.getName(), new PutToBlackList(reqId, reaction, transactionData));
//                    break;
//
//                case ATTR_FRAUD_LIST:
////                    this.reactionThreadPool.submit(new PutToFraudList(reqId, reaction, transactionData));
//                    dictionaryData.put(ATTR_FRAUD_LIST.getName(), new PutToFraudList(reqId, reaction, transactionData));
//                    break;
//
//                default:
//                    break;
//            }
//
//            log.info(
//                    String.format(
//                            "Finish executing reaction id [%s] action[%s] action value: [%s] result: [EXECUTED]",
//                            reaction.getId(), reaction.getAction(), reaction.getActionValue()
//                    )
//            );
//        }
//
//        return dictionaryData;
//    }
}
