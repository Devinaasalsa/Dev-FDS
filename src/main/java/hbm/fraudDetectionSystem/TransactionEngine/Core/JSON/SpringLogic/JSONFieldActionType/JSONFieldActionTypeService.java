package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldActionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class JSONFieldActionTypeService {
    protected final JSONFieldActionTypeRepository jsonFieldActionTypeRepository;

    @Autowired
    public JSONFieldActionTypeService(JSONFieldActionTypeRepository jsonFieldActionTypeRepository) {
        this.jsonFieldActionTypeRepository = jsonFieldActionTypeRepository;
    }

    public List<JSONFieldActionType> findAll() {
        return this.jsonFieldActionTypeRepository.findAllByOrderByDescriptionAsc();
    }
}
