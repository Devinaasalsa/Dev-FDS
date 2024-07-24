package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldFormatter;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule.Rule;
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
@RequestMapping("json_field_formatter")
public class JSONFieldFormatterResource extends ResponseResourceEntity<JSONFieldFormatter> {
    protected final JSONFieldFormatterService jsonFieldFormatterService;

    @Autowired
    public JSONFieldFormatterResource(JSONFieldFormatterService jsonFieldFormatterService) {
        this.jsonFieldFormatterService = jsonFieldFormatterService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<JSONFieldFormatter> fetchedData = jsonFieldFormatterService.findAll();
            httpStatus = OK;
            httpMessage = "Data Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
