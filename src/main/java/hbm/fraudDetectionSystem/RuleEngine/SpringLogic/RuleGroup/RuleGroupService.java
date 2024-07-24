package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleGroup;

import java.util.List;
import java.util.Map;

public interface RuleGroupService {

    List<RuleGroup> listRulesGroup(long groupId);

    List<RuleGroup> fetchRuleGroupByUserGroup(long userGroupId);

    void addRuleGroup(RuleGroup ruleGroup);

    void updateRuleGroup(RuleGroup ruleGroup);

    void deleteRulesGroup(long id);

    List<RuleGroup> searchRuleGroup(Map<String, Object> reqBody);
}
