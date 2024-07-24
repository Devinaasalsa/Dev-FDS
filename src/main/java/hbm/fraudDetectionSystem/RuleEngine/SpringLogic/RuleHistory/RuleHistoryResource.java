package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleHistory;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/rule_history")
public class RuleHistoryResource extends ResponseResourceEntity<RuleHistory> {
    protected final RuleHistoryService ruleHistoryService;

    @Autowired
    public RuleHistoryResource(RuleHistoryService ruleHistoryService) {
        this.ruleHistoryService = ruleHistoryService;
    }

    @GetMapping("/findByRuleId")
    public ResponseEntity<?> findByRuleId(@RequestParam("ruleId") long ruleId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<RuleHistory> fetchedData = ruleHistoryService.fetchRuleHistoryByUtrnno(ruleId);
            httpStatus = OK;
            httpMessage = "Rule History Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
