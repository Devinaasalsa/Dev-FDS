package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleTriggered;

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
@RequestMapping("/rule_triggered")
public class RuleTriggeredResource extends ResponseResourceEntity<RuleTriggered> {
    protected final RuleTriggeredService ruleTriggeredService;

    @Autowired
    public RuleTriggeredResource(RuleTriggeredService ruleTriggeredService) {
        this.ruleTriggeredService = ruleTriggeredService;
    }

    @GetMapping("/findByUtrnno")
    public ResponseEntity<?> findByUtrnno(@RequestParam("utrnno") long utrnno) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<RuleTriggered> fetchedData = ruleTriggeredService.fetchRuleTriggeredByUtrnno(utrnno);
            httpStatus = OK;
            httpMessage = "Rule Triggered Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
