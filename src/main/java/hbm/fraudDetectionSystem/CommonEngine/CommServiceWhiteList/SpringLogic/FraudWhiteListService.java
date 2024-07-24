package hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.SpringLogic;


import hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.exception.WhiteListExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.exception.WhiteListNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface FraudWhiteListService {


    List<FraudWhiteList>listAllWhiteList();

    FraudWhiteList addWhiteList(String entityType, String value, Long userGroupId, Timestamp dateIn, Timestamp dateOut, Long initiatorId, String Reason);

    FraudWhiteList addWhiteListSystem(String entityType, String value, Timestamp dateIn, Timestamp dateOut, String Reason);

    FraudWhiteList updateWhiteList(Long currentId, String newEntityType, Timestamp dateIn, Timestamp dateOut, String newValue, Long newUserGroupId, Long newInitiatorId, String newReason) throws WhiteListExistException, WhiteListNotFoundException;

    void deleteWhiteListValue(long id);

    String run(Map<String, String> preparedData);

    void saveWhiteListToDatabase(MultipartFile file, long initiatorId, long uGroupId) throws IOException;

    List<FraudWhiteList> searchWhiteList(Map<String, Object> reqBody);
}
