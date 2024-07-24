package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.AlertCaseAnalytic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlertCaseAnalyticService {
    protected final AlertCaseAnalyticRepository alertCaseAnalyticRepository;

    @Autowired
    public AlertCaseAnalyticService(AlertCaseAnalyticRepository alertCaseAnalyticRepository) {
        this.alertCaseAnalyticRepository = alertCaseAnalyticRepository;
    }

    public AlertCaseAnalytic fetchAllData() {
        return this.alertCaseAnalyticRepository.findById(1L).orElseThrow(() ->
                new  RuntimeException("Alert Case Analytic not found")
        );
    }
}
