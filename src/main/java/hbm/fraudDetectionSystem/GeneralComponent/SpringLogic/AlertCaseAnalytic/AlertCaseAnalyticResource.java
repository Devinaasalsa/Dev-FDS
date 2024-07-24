package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.AlertCaseAnalytic;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
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
@RequestMapping(path = "/alert_case_analytic")
public class AlertCaseAnalyticResource extends ResponseResourceEntity<AlertCaseAnalytic> {
    protected final AlertCaseAnalyticService alertCaseAnalyticService;

    @Autowired
    public AlertCaseAnalyticResource(AlertCaseAnalyticService alertCaseAnalyticService) {
        this.alertCaseAnalyticService = alertCaseAnalyticService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> fetchAllData() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            AlertCaseAnalytic fetchedData = alertCaseAnalyticService.fetchAllData();
            httpStatus = OK;
            httpMessage = SUCCESS_FETCH_MESSAGE;
            return responseWithData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithData(httpStatus, httpMessage, null);
        }
    }
}
