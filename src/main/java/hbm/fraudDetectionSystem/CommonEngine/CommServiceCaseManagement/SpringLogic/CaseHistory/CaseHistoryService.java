package hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.SpringLogic.CaseHistory;


import hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.exception.InitiatorException;

import java.sql.Timestamp;
import java.util.List;

public interface CaseHistoryService {
    CaseHistory addCaseHistory(String actionType, String initiator, Timestamp actionDate, String info, long caseId) throws InitiatorException;

    List<CaseHistory> findAllByCaseId(long caseId);
}
