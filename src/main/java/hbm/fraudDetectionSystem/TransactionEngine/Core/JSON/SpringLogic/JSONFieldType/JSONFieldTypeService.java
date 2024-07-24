package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class JSONFieldTypeService {
    protected final JSONFieldTypeRepository jsonFieldTypeRepository;

    @Autowired
    public JSONFieldTypeService(JSONFieldTypeRepository jsonFieldTypeRepository) {
        this.jsonFieldTypeRepository = jsonFieldTypeRepository;
    }

    public List<JSONFieldType> findAll() {
        return this.jsonFieldTypeRepository.findAllByOrderByDescriptionAsc();
    }
}
