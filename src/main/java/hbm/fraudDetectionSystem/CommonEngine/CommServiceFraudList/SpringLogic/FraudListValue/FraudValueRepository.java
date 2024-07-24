package hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListValue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FraudValueRepository extends JpaRepository<FraudValue, Long> {
    FraudValue findSanctionListValueByValue(String value);
    FraudValue findAllById(long id);
    List<FraudValue> findAllByListIdListId(long listId);
    List<FraudValue> findAllByListId_ListName(String listName);
    List<FraudValue> findByOrderByIdAsc();
    FraudValue findByValue(String value);

    @Query(
            value = "select tflv.value from t_fraud_list_value tflv, t_fraud_list tfl where tflv.list_id = tfl.list_id and tfl.list_name = :listName",
            nativeQuery = true
    )
    List<String> findFraudValueByListName(String listName);

    Optional<FraudValue> findByValueAndListIdListId(String value, long listId);
}
