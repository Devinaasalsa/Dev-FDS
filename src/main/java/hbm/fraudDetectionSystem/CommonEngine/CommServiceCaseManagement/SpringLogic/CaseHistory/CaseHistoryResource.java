package hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.SpringLogic.CaseHistory;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.SpringLogic.Case.Case;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/caseHistory")
public class CaseHistoryResource extends ResponseResourceEntity<CaseHistory> {
    private CaseHistoryService historyService;

    @Autowired
    public CaseHistoryResource(CaseHistoryService historyService) {
        this.historyService = historyService;
    }

    @PostMapping("/addCaseHistory")
    public ResponseEntity<?> addCaseHistory(@RequestBody CaseHistory caseHistory){
        HttpStatus httpStatus;
        String httpMessage;
        try {
            historyService.addCaseHistory(caseHistory.getActionType(), caseHistory.getInitiator(), caseHistory.getActionDate(), caseHistory.getInfo(), caseHistory.getCaseId());
            httpStatus = OK;
            httpMessage = "add Case History Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @GetMapping("/find/{caseId}")
    public ResponseEntity<?> getCaseId(@PathVariable("caseId") long caseId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<CaseHistory> caseHistory = historyService.findAllByCaseId(caseId);
            httpStatus = OK;
            httpMessage = "Find by CaseId";
            return responseWithListData(httpStatus, httpMessage, caseHistory);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

}
