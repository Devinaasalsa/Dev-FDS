package hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListValue;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.FraudListExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.FraudValueExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.FraudValueNotFound;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FraudValueService {

    FraudValue addValue(String value, String author, Long listId) throws FraudListExistException, FraudValueNotFound, FraudValueExistException;

    List<FraudValue> getSanctionListValue();

    FraudValue updateValue(FraudValue value) throws  FraudListExistException, FraudValueNotFound, FraudValueExistException;

    void deleteFraudListValue(Long id);

    FraudValue findAllById(long id);

    FraudValue findByValue(String value);

    List<FraudValue> findAllByListId(long listId);

    void saveFraudValueToDatabase(long listId, String author, MultipartFile file) throws IOException;

    List<FraudValue> findAllByListName(String listName);

}
