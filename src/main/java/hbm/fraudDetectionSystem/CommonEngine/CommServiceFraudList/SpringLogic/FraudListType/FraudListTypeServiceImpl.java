package hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class FraudListTypeServiceImpl implements FraudListTypeService{
    private FraudListTypeRepository repository;

    @Autowired
    public FraudListTypeServiceImpl(FraudListTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<FraudListType> listAllEntityType() {
        return repository.findByOrderByTypeIdAsc();
    }

    @Override
    public FraudListType findEntityTypeByTypeId(int typeId) {
        return repository.findByTypeId(typeId);
    }

}
