package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldConfiguration;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.HttpResponse;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static hbm.fraudDetectionSystem.GeneralComponent.Constant.ResponseResourceMessage.*;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(path = "/iso_field_configuration")
public class FieldConfigurationResource extends ResponseResourceEntity<FieldConfiguration> {
    private final FieldConfigurationService fieldConfigurationService;

    @Autowired
    public FieldConfigurationResource(FieldConfigurationService fieldConfigurationService) {
        this.fieldConfigurationService = fieldConfigurationService;
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<HttpResponse<List<FieldConfiguration>>> findAllISOFieldConfiguration(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<FieldConfiguration> fetchedData = fieldConfigurationService.findAllData(id);
            httpStatus = OK;
            httpMessage = SUCCESS_FETCH_MESSAGE;
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new LinkedList<>());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<HttpResponse<FieldConfiguration>> addISOFieldConfiguration(@RequestBody FieldConfiguration requestData) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            fieldConfigurationService.saveData(requestData);
            httpStatus = OK;
            httpMessage = SUCCESS_ADD_MESSAGE;
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
        }
        return response(httpStatus, httpMessage);
    }

    @PostMapping("/update")
    public ResponseEntity<HttpResponse<FieldConfiguration>> updateISOFieldConfiguration(@RequestBody FieldConfiguration requestData) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            fieldConfigurationService.updateData(requestData);
            httpStatus = OK;
            httpMessage = SUCCESS_UPDATE_MESSAGE;
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
        }
        return response(httpStatus, httpMessage);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpResponse<FieldConfiguration>> deleteISOFieldConfiguration(@PathVariable("id") Long requestData) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            fieldConfigurationService.deleteData(requestData);
            httpStatus = OK;
            httpMessage = SUCCESS_DELETE_MESSAGE(1);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            e.printStackTrace();
        }
        return response(httpStatus, httpMessage);
    }

    @PostMapping("/search")
    public ResponseEntity<?> findAllISOFieldConfiguration(@RequestBody Map<String, Object> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<FieldConfiguration> fetchedData = fieldConfigurationService.search(reqBody);
            httpStatus = OK;
            httpMessage = SUCCESS_FETCH_MESSAGE;
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new LinkedList<>());
        }
    }
}
