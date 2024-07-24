package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.HeaderConfiguration;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.HttpResponse;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static hbm.fraudDetectionSystem.GeneralComponent.Constant.ResponseResourceMessage.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/header_configuration")
public class HeaderConfigurationResource extends ResponseResourceEntity<HeaderConfiguration> {
    private final HeaderConfigurationService headerConfigurationService;

    @Autowired
    public HeaderConfigurationResource(HeaderConfigurationService headerConfigurationService) {
        this.headerConfigurationService = headerConfigurationService;
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<HttpResponse<List<HeaderConfiguration>>> findAllISOHeaderConfiguration(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<HeaderConfiguration> fetchedData = headerConfigurationService.findAllData(id);
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
    public ResponseEntity<HttpResponse<HeaderConfiguration>> addISOHeaderConfiguration(@RequestBody HeaderConfiguration requestData) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            headerConfigurationService.saveData(requestData);
            httpStatus = OK;
            httpMessage = SUCCESS_ADD_MESSAGE;
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            e.printStackTrace();
        }
        return response(httpStatus, httpMessage);
    }

    @PostMapping("update")
    public ResponseEntity<HttpResponse<HeaderConfiguration>> updateISOHeaderConfiguration(@RequestBody HeaderConfiguration requestData) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            headerConfigurationService.updateData(requestData);
            httpStatus = OK;
            httpMessage = SUCCESS_UPDATE_MESSAGE;
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
        }
        return response(httpStatus, httpMessage);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<HttpResponse<HeaderConfiguration>> deleteISOHeaderConfiguration(@PathVariable("id") Long requestData) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            headerConfigurationService.deleteData(requestData);
            httpStatus = OK;
            httpMessage = SUCCESS_DELETE_MESSAGE(1);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
        }
        return response(httpStatus, httpMessage);
    }

    @PostMapping("search")
    public ResponseEntity<?> search(@RequestBody Map<String, Object> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<HeaderConfiguration> headerConfigurations = headerConfigurationService.search(reqBody);
            httpStatus = OK;
            httpMessage = SUCCESS_FETCH_MESSAGE;
            return responseWithListData(httpStatus, httpMessage, headerConfigurations);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
