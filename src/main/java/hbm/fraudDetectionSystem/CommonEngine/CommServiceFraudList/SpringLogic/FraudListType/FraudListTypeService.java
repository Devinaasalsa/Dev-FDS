package hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListType;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FraudListTypeService {
    List<FraudListType>listAllEntityType();

    FraudListType findEntityTypeByTypeId(int typeId);

}
