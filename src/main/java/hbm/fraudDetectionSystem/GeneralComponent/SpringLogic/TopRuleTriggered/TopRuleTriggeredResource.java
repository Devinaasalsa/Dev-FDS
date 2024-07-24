package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TopRuleTriggered;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransactionActivity.TransactionActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

import static hbm.fraudDetectionSystem.GeneralComponent.Constant.ResponseResourceMessage.SUCCESS_FETCH_MESSAGE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/top_rule_triggered")
public class TopRuleTriggeredResource extends ResponseResourceEntity<TopRuleTriggered> {
    protected final TopRuleTriggeredService topRuleTriggeredService;

    @Autowired
    public TopRuleTriggeredResource(TopRuleTriggeredService topRuleTriggeredService) {
        this.topRuleTriggeredService = topRuleTriggeredService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> fetchAllData() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<TopRuleTriggered> fetchedData = topRuleTriggeredService.fetchAllData();
            httpStatus = OK;
            httpMessage = SUCCESS_FETCH_MESSAGE;
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new LinkedList<>());
        }
    }
}
