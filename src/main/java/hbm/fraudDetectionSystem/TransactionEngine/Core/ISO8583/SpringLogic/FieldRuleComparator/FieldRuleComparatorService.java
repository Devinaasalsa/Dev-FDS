package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleComparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class FieldRuleComparatorService {
    protected final FieldRuleComparatorRepository fieldRuleComparatorRepository;

    @Autowired
    public FieldRuleComparatorService(FieldRuleComparatorRepository fieldRuleComparatorRepository) {
        this.fieldRuleComparatorRepository = fieldRuleComparatorRepository;
    }

    public List<FieldRuleComparator> findAllData() {
        return fieldRuleComparatorRepository.findAll();
    }
}
