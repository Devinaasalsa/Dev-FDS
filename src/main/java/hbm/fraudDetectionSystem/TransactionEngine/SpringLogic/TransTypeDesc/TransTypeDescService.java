package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransTypeDesc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TransTypeDescService {
    protected final TransTypeDescRepository transTypeDescRepository;

    @Autowired
    public TransTypeDescService(TransTypeDescRepository transTypeDescRepository) {
        this.transTypeDescRepository = transTypeDescRepository;
    }

    public List<TransTypeDesc> fetchAllTransType() {
        return transTypeDescRepository.findAll();
    }
}
