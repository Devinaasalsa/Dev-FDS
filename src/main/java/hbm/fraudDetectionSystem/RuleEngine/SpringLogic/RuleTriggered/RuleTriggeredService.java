package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleTriggered;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class RuleTriggeredService {
    protected final RuleTriggeredRepository ruleTriggeredRepository;

    @Autowired
    public RuleTriggeredService(RuleTriggeredRepository ruleTriggeredRepository) {
        this.ruleTriggeredRepository = ruleTriggeredRepository;
    }

    public List<RuleTriggered> fetchRuleTriggeredByUtrnno(long utrnno) {
        return this.ruleTriggeredRepository.findAllByUtrnnoOrderByIdAsc(utrnno);
    }
}
