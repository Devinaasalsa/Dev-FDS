package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TopRuleTriggered;

import hbm.fraudDetectionSystem.GeneralComponent.Utility.ReadOnlyRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopRuleTriggeredRepository extends ReadOnlyRepository<TopRuleTriggered, Long> {
}
