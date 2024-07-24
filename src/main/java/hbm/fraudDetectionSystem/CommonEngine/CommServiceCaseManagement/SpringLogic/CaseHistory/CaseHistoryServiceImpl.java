package hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.SpringLogic.CaseHistory;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.exception.InitiatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.constant.CaseConstant.INITIATOR_NOT_FOUND;

@Service
public class CaseHistoryServiceImpl implements CaseHistoryService {
    private CaseHistoryRepository historyRepository;

    @Autowired
    public CaseHistoryServiceImpl(CaseHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Override
    public CaseHistory addCaseHistory(String actionType, String initiator, Timestamp actionDate, String info, long caseId) throws InitiatorException {
        CaseHistory history = new CaseHistory();
        String initiatorAct = validateInitiator(initiator);
        history.setActionType(actionType);
        history.setInitiator(initiatorAct);
        history.setActionDate(actionDate);
        history.setInfo(info);
        history.setCaseId(caseId);
        historyRepository.save(history);
        return history;
    }

    @Override
    public List<CaseHistory> findAllByCaseId(long caseId) {
        return historyRepository.findByCaseIdOrderByActionDateDesc(caseId);
    }


    private String validateInitiator(String initiator) throws InitiatorException {
        if (initiator == null){
            throw new InitiatorException(INITIATOR_NOT_FOUND);
        }
        return initiator;
    }
}
