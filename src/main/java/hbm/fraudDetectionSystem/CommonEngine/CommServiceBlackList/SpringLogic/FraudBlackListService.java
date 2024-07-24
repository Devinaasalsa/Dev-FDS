package hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.SpringLogic;


import hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.exception.BlackListExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.exception.BlackListNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface FraudBlackListService {

    List<FraudBlackList>listAllBlackList();

    String run(Map<String, String>preparedData);

    FraudBlackList addBlackList(String entityType, String value, Long userGroupId, Timestamp dateIn, Timestamp dateOut, Long initiatorId, String Reason);

    FraudBlackList addBlackListSystem(String entityType, String value, Timestamp dateIn, Timestamp dateOut, String Reason);

    FraudBlackList updateBlackList(Long currentId, String newEntityType,Timestamp dateIn, Timestamp dateOut, String newValue, Long newUserGroupId, Long newInitiatorId, String newReason) throws BlackListNotFoundException, BlackListExistException;

    void deleteBlackListValue(long id);

    void saveBlackListToDatabase(MultipartFile file, long initiatorId, long uGroupId) throws IOException;

    List<FraudBlackList> searchFraudBlackList(Map<String, Object> reqBody);
}
