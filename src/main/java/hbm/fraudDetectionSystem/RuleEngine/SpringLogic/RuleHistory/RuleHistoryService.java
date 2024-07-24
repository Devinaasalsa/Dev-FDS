package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class RuleHistoryService {
    protected final RuleHistoryRepository ruleHistoryRepository;

    @Autowired
    public RuleHistoryService(RuleHistoryRepository ruleHistoryRepository) {
        this.ruleHistoryRepository = ruleHistoryRepository;
    }

    public List<RuleHistory> fetchRuleHistoryByUtrnno(long ruleId) {
        return this.ruleHistoryRepository.findAllByRuleIdOrderByTimestampDesc(ruleId);
    }

    public void add(RuleHistory history) {
        ruleHistoryRepository.save(history);
    }
}
