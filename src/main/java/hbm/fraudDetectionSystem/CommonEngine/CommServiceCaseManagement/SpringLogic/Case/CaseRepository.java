package hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.SpringLogic.Case;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.constant.CaseQueryConstant.*;

public interface CaseRepository extends JpaRepository<Case,Long> {

    Case findAllByCaseId(long id);

    Case findByUtrnno(long utrnno);

    Case findByLockedBy(String initiator);

    @Query(
            value = FIND_CASE_BY_LOCKEDBY_USERNAME_ORACLE,
            nativeQuery = true
    )
    Map<String, Object> findCaseByLockedBy(@Param("initiator") String initiator);

    @Query(value = FIND_BY_ORDER_BY_LASTUPDATE, nativeQuery = true)
    List<Map<Object,Object>> findByOrderByLastUpdateDesc();

    @Query(
            value = FIND_BY_LOCKEDBY_USERNAME_ORACLE,
            nativeQuery = true
    )
    List<Map<Object,Object>> findCaseOrderByLockedByAndCaseId(@Param("userId")Long id, @Param("username")String username);

    @Query(value = REPORT_ALERT_STATISTIC_BY_RULES, nativeQuery = true)
    List<Object>findReportAlertStatisticByRules(@Param("reportStartDate")Timestamp reportStartDate, @Param("reportEndDate")Timestamp reportEndDate);

    @Query(value = REPORT_ALERT_STATISTIC_TOTAL, nativeQuery = true)
    List<Object>findReportAlertStatisticTotal(@Param("reportStartDate")Timestamp reportStartDate, @Param("reportEndDate")Timestamp reportEndDate);

    @Query(value = REPORT_NOT_ALERTED_ALERTED, nativeQuery = true)
    List<Object>findReportNotAlertedAlerted(@Param("reportStartDate")Timestamp reportStartDate, @Param("reportEndDate")Timestamp reportEndDate);
}
