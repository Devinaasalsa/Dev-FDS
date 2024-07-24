package hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudList;


import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.FraudListExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.ListFraudNameNotFound;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FraudListService {
    FraudList addList(FraudList fraudList) throws ListFraudNameNotFound, FraudListExistException;

    List<FraudList> getSanctionList();

    List<FraudList>getListByEntityType(int entityType);

    FraudList updateList(String currentListName, FraudList fraudList) throws ListFraudNameNotFound, FraudListExistException;

    FraudList findListId(Long listId);

    void deleteSanctionList(Long listId);

    List<FraudList> searchFraudList(Map<String, Object> reqBody);
}
