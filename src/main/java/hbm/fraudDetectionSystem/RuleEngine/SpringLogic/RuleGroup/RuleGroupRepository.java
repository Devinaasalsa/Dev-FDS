package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleGroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RuleGroupRepository extends JpaRepository<RuleGroup, Long> {
    RuleGroup findAllById(long id);

    List<RuleGroup> findByUserGroupIdOrderByIdAsc(long uGroupId);

    List<RuleGroup> findAllByUserGroupIdOrderByPriorityAsc(long userGroupId);

    List<RuleGroup> findAllByIsActiveTrueOrderByPriorityAsc();

    Optional<RuleGroup> findRuleGroupByGroupName(String name);
}
