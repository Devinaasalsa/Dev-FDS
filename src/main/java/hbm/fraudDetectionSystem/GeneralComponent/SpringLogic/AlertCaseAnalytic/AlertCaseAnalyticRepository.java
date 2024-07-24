package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.AlertCaseAnalytic;

import hbm.fraudDetectionSystem.GeneralComponent.Utility.ReadOnlyRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertCaseAnalyticRepository extends ReadOnlyRepository<AlertCaseAnalytic, Long> {

}
