package hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.SpringLogic.Case;


import hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.exception.*;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule.Rule;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface CaseService {

    List<Map<Object, Object>> findAllCase();
    List<Map<Object, Object>> findCaseOrderByLockedByAndCaseId(String username);
    Case findAllByUsername(String username);

    Map<String, Object> findCaseByUsername(String username);

    Case findByUtrnno(long utrnno);

    Case findByCaseId(long caseId);
    Case lockCase(long caseId, String username) throws Exception;

    Case unlockCase(long caseId, String username) throws Exception;

    Case clasifyAlert(long caseId, String initiator, int clasType, String clasifiedComment) throws Exception;

    Case takeAction(long caseId, String initiator, int actionType, String caseComment, String listId, String value, String reason, String entityType, String datein, String dateout, String userGroupId) throws Exception;

    Case takeActionForwardedTo(Long caseId, String initiator, int actionType, String caseComment, long userForwardedTo, String listId, String value, String reason, String entityType, String datein, String dateout, String userGroupId) throws Exception;

    void generateCase(Map<String, String> transDetails) throws InitiatorException;

    List<Object>generateReportAlertStatisticByRule(Timestamp reportStartDate, Timestamp reportEndDate);

    List<Object>generateReportAlertStatisticTotal(Timestamp reportStartDate, Timestamp reportEndDate);

    List<Object>generateReportNotAlertedAlerted(Timestamp reportStartDate, Timestamp reportEndDate);

    List<Map<String, Object>> searchAlert(Map<String, Object> reqBody);
}
