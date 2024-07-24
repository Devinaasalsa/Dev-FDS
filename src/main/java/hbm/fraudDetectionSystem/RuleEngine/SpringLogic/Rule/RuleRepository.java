package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {

    List<Rule> findByOrderByRuleIdAsc();

    Rule findAllByRuleId(long id);

    List<Rule> findAllByRuleIdIn(List<Long> id);

    Optional<Rule> findByRuleName(String ruleName);

    @Query(
            value = "select * from t_rule where rule_group_id in :groupIds order by rule_id asc",
            nativeQuery = true
    )
    List<Rule> findAllByRuleGroupIds(List<Long> groupIds);

    List<Rule> findAllByRuleGroupIdOrderByPriority(long groupId);

    List<Rule> findAllByRuleGroupIdAndIsActiveTrueOrderByPriority(long groupId);
}
