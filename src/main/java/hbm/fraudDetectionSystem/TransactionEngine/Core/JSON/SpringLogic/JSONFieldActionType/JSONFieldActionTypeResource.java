package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldActionType;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldFormatter.JSONFieldFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("json_action_type")
public class JSONFieldActionTypeResource extends ResponseResourceEntity<JSONFieldActionType> {
    protected final JSONFieldActionTypeService jsonFieldActionTypeService;

    @Autowired
    public JSONFieldActionTypeResource(JSONFieldActionTypeService jsonFieldActionTypeService) {
        this.jsonFieldActionTypeService = jsonFieldActionTypeService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<JSONFieldActionType> fetchedData = jsonFieldActionTypeService.findAll();
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
