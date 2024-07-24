package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule;

import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleGroup.RuleGroup;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface RuleService {
    List<Rule> getAllRules();

    List<Rule> fetchRulesByUserGroup(long groupId);

    List<Rule> fetchRulesByRuleGroup(long groupId);

    void add(Rule rule);

    void update(Rule rule);

    void delete(long ruleId);

    Rule findRuleById(long id);

    List<Rule> findAllRuleId(List<Long> id);

    void activation(long ruleId, String initiator, String comment);

    void deactivation(long ruleId, String initiator, String comment);

    void approval(long ruleId, String initiator, String comment);

    void rejection(long ruleId, String initiator, String comment);

    byte[] export(List<Long> id) throws IOException;

    void importRule(MultipartFile file) throws IOException;

    List<Rule> searchRule(Map<String, Object> reqBody);
}
