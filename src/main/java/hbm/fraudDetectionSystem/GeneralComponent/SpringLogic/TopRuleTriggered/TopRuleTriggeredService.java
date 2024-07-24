package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TopRuleTriggered;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopRuleTriggeredService {
    protected final TopRuleTriggeredRepository topRuleTriggeredRepository;

    @Autowired
    public TopRuleTriggeredService(TopRuleTriggeredRepository topRuleTriggeredRepository) {
        this.topRuleTriggeredRepository = topRuleTriggeredRepository;
    }

    public List<TopRuleTriggered> fetchAllData() {
        return this.topRuleTriggeredRepository.findAll();
    }
}
