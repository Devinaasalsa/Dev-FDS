package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.FraudReaction.FraudReactionRepository;
import hbm.fraudDetectionSystem.RuleEngine.Constant.RuleConstant;
import hbm.fraudDetectionSystem.RuleEngine.Enum.RuleState;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleGroup.RuleGroup;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleGroup.RuleGroupRepository;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleHistory.RuleHistory;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleHistory.RuleHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RuleServiceImpl implements RuleService {
    @PersistenceContext
    protected EntityManager em;
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final RuleRepository ruleRepository;
    protected final RuleGroupRepository ruleGroupRepository;
    protected final RuleHistoryService ruleHistoryService;
    protected final FraudReactionRepository fraudReactionRepository;


    @Autowired
    public RuleServiceImpl(RuleRepository ruleRepository, RuleGroupRepository ruleGroupRepository, RuleHistoryService ruleHistoryService, FraudReactionRepository fraudReactionRepository) {
        this.ruleRepository = ruleRepository;
        this.ruleGroupRepository = ruleGroupRepository;
        this.ruleHistoryService = ruleHistoryService;
        this.fraudReactionRepository = fraudReactionRepository;
    }

    @Override
    public List<Rule> getAllRules() {
        return ruleRepository.findByOrderByRuleIdAsc();
    }

    @Override
    public List<Rule> fetchRulesByUserGroup(long groupId) {
        List<RuleGroup> ruleGroups = ruleGroupRepository.findAllByUserGroupIdOrderByPriorityAsc(groupId);
        return ruleRepository.findAllByRuleGroupIds(
                ruleGroups.stream()
                        .map(RuleGroup::getId)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<Rule> fetchRulesByRuleGroup(long groupId) {
        return ruleRepository.findAllByRuleGroupIdOrderByPriority(groupId);
    }

    @Override
    public void add(Rule rule) {
        validateRuleGroup(rule.getRuleGroup().getId());
        validateRuleName(rule.getRuleName(), 0L);
        rule.setIsActive(false);
        rule.setStatus(RuleConstant.CREATE_STAT);
        rule.setState(RuleState.JUST_CREATE);
        Rule aRule = ruleRepository.save(rule);

        ruleHistoryService.add(
                RuleHistory.builder()
                        .ruleId(aRule.getRuleId())
                        .initiator(rule.getAuthor())
                        .status(RuleConstant.CREATE_STAT)
                        .timestamp(new Timestamp(System.currentTimeMillis()))
                        .description(RuleConstant.CREATE_HISTORY_MSG(rule.getAuthor()))
                        .build()
        );
    }

    @Override
    public void update(Rule rule) {
        validateRuleId(rule.getRuleId());
        validateRuleGroup(rule.getRuleGroup().getId());
        validateRuleName(rule.getRuleName(), rule.getRuleId());
        rule.setIsActive(false);
        rule.setStatus(RuleConstant.UPDATE_STAT);
        rule.setState(RuleState.JUST_UPDATE);
        ruleRepository.save(rule);

        ruleHistoryService.add(
                RuleHistory.builder()
                        .ruleId(rule.getRuleId())
                        .initiator(rule.getAuthor())
                        .status(RuleConstant.UPDATE_STAT)
                        .timestamp(new Timestamp(System.currentTimeMillis()))
                        .description(RuleConstant.UPDATE_HISTORY_MSG(rule.getAuthor()))
                        .build()
        );
    }

    @Override
    public void delete(long id) {
        ruleRepository.deleteById(id);
        fraudReactionRepository.deleteAllByBindingTypeAndBindingId("RULE", id);
    }

    @Override
    public Rule findRuleById(long id) {
        return ruleRepository.findAllByRuleId(id);
    }

    @Override
    public List<Rule> findAllRuleId(List<Long> id) {
        return ruleRepository.findAllByRuleIdIn(id);
    }

    @Override
    public void activation(long ruleId, String initiator, String comment) {
        Rule rule = ruleRepository.findAllByRuleId(ruleId);

        if (rule != null) {
            rule.setState(RuleState.ACTIVATION);
            rule.setStatus(RuleConstant.WAITING_CONFIRM_STAT);
            ruleRepository.save(rule);

            ruleHistoryService.add(
                    RuleHistory.builder()
                            .ruleId(ruleId)
                            .initiator(rule.getAuthor())
                            .status(RuleConstant.WAITING_CONFIRM_STAT)
                            .timestamp(new Timestamp(System.currentTimeMillis()))
                            .description(RuleConstant.ACTIVATION_HISTORY_MSG(rule.getAuthor()))
                            .comment(comment)
                            .build()
            );
        } else {
            throw new RuntimeException("RuleId Not Found");
        }
    }

    @Override
    public void deactivation(long ruleId, String initiator, String comment) {
        Rule rule = ruleRepository.findAllByRuleId(ruleId);

        if (rule != null) {
            rule.setState(RuleState.DEACTIVATION);
            rule.setStatus(RuleConstant.WAITING_CONFIRM_STAT);
            ruleRepository.save(rule);

            ruleHistoryService.add(
                    RuleHistory.builder()
                            .ruleId(ruleId)
                            .initiator(rule.getAuthor())
                            .status(RuleConstant.WAITING_CONFIRM_STAT)
                            .timestamp(new Timestamp(System.currentTimeMillis()))
                            .description(RuleConstant.DEACTIVATION_HISTORY_MSG(rule.getAuthor()))
                            .comment(comment)
                            .build()
            );
        } else {
            throw new RuntimeException("RuleId Not Found");
        }
    }

    @Override
    public void approval(long ruleId, String initiator, String comment) {
        Rule rule = ruleRepository.findAllByRuleId(ruleId);

        if (rule != null) {
            switch (rule.getState()) {
                case JUST_CREATE:
                case JUST_UPDATE:
                case ACTIVATION:
                    rule.setIsActive(true);
                    break;

                case DEACTIVATION:
                    rule.setIsActive(false);
                    break;
            }

            rule.setState(RuleState.APPROVED);
            rule.setStatus(RuleConstant.APPROVED_STAT);
            ruleRepository.save(rule);

            ruleHistoryService.add(
                    RuleHistory.builder()
                            .ruleId(ruleId)
                            .initiator(rule.getAuthor())
                            .status(RuleConstant.APPROVED_STAT)
                            .timestamp(new Timestamp(System.currentTimeMillis()))
                            .description(RuleConstant.APPROVE_CONFIRMATION_HISTORY_MSG(rule.getAuthor()))
                            .comment(comment)
                            .build()
            );
        } else {
            throw new RuntimeException("RuleId Not Found");
        }
    }

    @Override
    public void rejection(long ruleId, String initiator, String comment) {
        Rule rule = ruleRepository.findAllByRuleId(ruleId);

        if (rule != null) {
            rule.setState(RuleState.REJECTED);
            rule.setStatus(RuleConstant.REJECTED_STAT);
            ruleRepository.save(rule);

            ruleHistoryService.add(
                    RuleHistory.builder()
                            .ruleId(ruleId)
                            .initiator(rule.getAuthor())
                            .status(RuleConstant.REJECTED_STAT)
                            .timestamp(new Timestamp(System.currentTimeMillis()))
                            .description(RuleConstant.REJECTION_CONFIRMATION_HISTORY_MSG(rule.getAuthor()))
                            .comment(comment)
                            .build()
            );
        } else {
            throw new RuntimeException("RuleId Not Found");
        }
    }

    @Override
    public byte[] export(List<Long> id) {
        List<Rule> getRuleId = ruleRepository.findAllByRuleIdIn(id);

        if (getRuleId.size() == 0) {
            throw new RuntimeException("RuleId not found");
        }

        List<Rule> tempRuleList = new LinkedList<>();

        getRuleId.forEach(v1 -> {
            tempRuleList.add(
                    new Rule().builder()
                            .ruleId(null)
                            .ruleName(v1.getRuleName() + "-1")
                            .description(v1.getDescription())
                            .isActive(v1.getIsActive())
                            .riskValue(v1.getRiskValue())
                            .dateFrom(v1.getDateFrom())
                            .dateTo(v1.getDateTo())
                            .priority(v1.getPriority())
                            .type(v1.getType())
                            .author(v1.getAuthor())
                            .sFormula(v1.getSFormula())
                            .status(v1.getStatus())
                            .state(v1.getState())
                            .ruleGroup(v1.getRuleGroup())
                            .ruleBodies(v1.getRuleBodies())
                            .build()
            );
        });

        byte[] jsonData = new Gson().toJson(tempRuleList).getBytes(StandardCharsets.UTF_8);

        return Base64.getEncoder().encode(jsonData);
    }

    @Override
    public void importRule(MultipartFile file) throws IOException {

        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        byte[] decrypt = Base64.getDecoder().decode(content);
        String decryptedContent = new String(decrypt, StandardCharsets.UTF_8);
        List<Rule> jsonForm = new Gson().fromJson(decryptedContent, new TypeToken<List<Rule>>() {
        }.getType());

        ruleRepository.saveAll(jsonForm);
    }

    @Override
    public List<Rule> searchRule(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<Rule> query = cb.createQuery(Rule.class);
        Root<Rule> root = query.from(Rule.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!value.toString().isEmpty()) {
                            switch (key) {
                                case "userGroup":
                                    long ruleGroupId = Long.parseLong(value.toString());
                                    List<RuleGroup> ruleGroups = ruleGroupRepository.findAllByUserGroupIdOrderByPriorityAsc(ruleGroupId);
                                    List<Long> ruleGroupIds = ruleGroups.stream()
                                            .map(RuleGroup::getId)
                                            .collect(Collectors.toList());

                                    if (!ruleGroupIds.isEmpty()) {
                                        Join<Rule, RuleGroup> ruleGroupJoin = root.join("ruleGroup");
                                        predicates.add(ruleGroupJoin.get("id").in(ruleGroupIds));
                                    }
                                    break;

                                case "dateFrom":
                                    String dateFrom = (String) value;
                                    if (!dateFrom.isEmpty()) {
                                        predicates.add(cb.greaterThanOrEqualTo(root.get("dateFrom"), Timestamp.valueOf(dateFrom)));
                                    }
                                    break;

                                case "dateTo":
                                    String dateTo = (String) value;
                                    if (!dateTo.isEmpty()) {
                                        predicates.add(cb.lessThanOrEqualTo(root.get("dateTo"), Timestamp.valueOf(dateTo)));
                                    }
                                    break;

                                case "ruleName":
                                    String likeValue = "%" + value + "%";
                                    predicates.add(cb.like(root.get(key), likeValue));
                                    break;

                                default:
                                    predicates.add(cb.equal(root.get(key), value));
                                    break;
                            }
                        }
                });

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<Rule> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    private void validateRuleId(long ruleId) {
        ruleRepository.findById(ruleId).orElseThrow(() -> new RuntimeException("RuleId Not Found"));
    }

    protected void validateRuleGroup(long groupId) {
        ruleGroupRepository.findById(groupId).orElseThrow(() ->
                new RuntimeException("Rule Group Id not found!"));
    }

    protected void validateRuleName(String ruleName, long ruleId) {
        ruleRepository.findByRuleName(ruleName).ifPresent(existingData -> {
            if (existingData.getRuleId() != ruleId) {
                throw new RuntimeException("Rule name already exist");
            }
        });
    }
}
