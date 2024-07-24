package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransactionActivity;

import hbm.fraudDetectionSystem.GeneralComponent.Utility.ReadOnlyRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionActivityRepository extends ReadOnlyRepository<TransactionActivity, Long> {
    @Query(
            value = "select *\n" +
                    "from transaction_activity\n" +
                    "where\n" +
                    "    converted_date BETWEEN to_date(:currentDate, 'YYYY-MM-DD') - INTERVAL '6 days' AND to_date(:currentDate, 'YYYY-MM-DD') order by converted_date asc;",
            nativeQuery = true
    )
    List<TransactionActivity> findAllByConvertedDateOrderByConvertedDateDesc(String currentDate);

//    @Query(
//            value = "SELECT *\n" +
//                    "FROM transaction_activity\n" +
//                    "WHERE converted_date BETWEEN SYSDATE - 6 AND SYSDATE\n" +
//                    "ORDER BY converted_date ASC",
//            nativeQuery = true
//    )
//    List<TransactionActivity> findAllByConvertedDateOrderByConvertedDateDesc();
}
