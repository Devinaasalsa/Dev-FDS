package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleHistoryRepository extends JpaRepository<RuleHistory, Long> {
    List<RuleHistory> findAllByRuleIdOrderByTimestampDesc(long ruleId);
}
