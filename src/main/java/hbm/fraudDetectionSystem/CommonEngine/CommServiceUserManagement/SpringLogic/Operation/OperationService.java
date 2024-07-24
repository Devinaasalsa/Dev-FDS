package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class OperationService {
    protected final OperationRepository operationRepository;

    @Autowired
    public OperationService(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    public List<Operation> findAllOperations() {
        return operationRepository.findAllByOrderByOpNameAsc();
    }

    public Set<Operation> getOperationByListId(List<Long> listId){
        return new HashSet<>(operationRepository.findAllById(listId));
    }
}
