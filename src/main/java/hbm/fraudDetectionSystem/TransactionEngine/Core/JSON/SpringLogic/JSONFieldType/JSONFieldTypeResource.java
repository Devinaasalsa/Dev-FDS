package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldType;

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
@RequestMapping("json_field_type")
public class JSONFieldTypeResource extends ResponseResourceEntity<JSONFieldType> {
    protected final JSONFieldTypeService jsonFieldTypeService;

    @Autowired
    public JSONFieldTypeResource(JSONFieldTypeService jsonFieldTypeService) {
        this.jsonFieldTypeService = jsonFieldTypeService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<JSONFieldType> fetchedData = jsonFieldTypeService.findAll();
            httpStatus = OK;
            httpMessage = "Data fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
