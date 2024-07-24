package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.HttpResponse;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static hbm.fraudDetectionSystem.GeneralComponent.Constant.ResponseResourceMessage.*;
import static hbm.fraudDetectionSystem.GeneralComponent.Utility.SpringLogicHelper.removeUnusefulExceptionString;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/msg_configuration")
public class MessageConfigurationResource extends ResponseResourceEntity<MessageConfiguration> {
    private final MessageConfigurationService messageConfigurationService;

    @Autowired
    public MessageConfigurationResource(MessageConfigurationService messageConfigurationService) {
        this.messageConfigurationService = messageConfigurationService;
    }

    @GetMapping("iso8583/list")
    public ResponseEntity<HttpResponse<List<MessageConfiguration>>> findAllISOConfiguration() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<MessageConfiguration> fetchedData = messageConfigurationService.findAllISO8583Configuration();
            httpStatus = OK;
            httpMessage = SUCCESS_FETCH_MESSAGE;
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new LinkedList<>());
        }
    }

    @GetMapping("json/list")
    public ResponseEntity<HttpResponse<List<MessageConfiguration>>> findAllJSONConfiguration() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<MessageConfiguration> fetchedData = messageConfigurationService.findAllJSONConfiguration();
            httpStatus = OK;
            httpMessage = SUCCESS_FETCH_MESSAGE;
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new LinkedList<>());
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<HttpResponse<MessageConfiguration>> findById(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            Optional<MessageConfiguration> fetchedData = messageConfigurationService.findExistingDataById(id);
            httpStatus = OK;
            httpMessage = SUCCESS_FETCH_MESSAGE;
            return responseWithData(httpStatus, httpMessage, fetchedData.get());
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithData(httpStatus, httpMessage, null);
        }
    }

    @PostMapping("add")
    public ResponseEntity<HttpResponse<MessageConfiguration>> addISOConfiguration(@RequestBody MessageConfiguration requestData) {
        HttpStatus httpStatus;
        String httpMessage;
        MessageConfiguration test = null;
        try {
            test = messageConfigurationService.saveData(requestData);
            httpStatus = OK;
            httpMessage = SUCCESS_ADD_MESSAGE;
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
        }
        return responseWithData(httpStatus, httpMessage, test);
    }

    @PostMapping("update")
    public ResponseEntity<HttpResponse<MessageConfiguration>> updateISOConfiguration(@RequestBody MessageConfiguration requestData) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            messageConfigurationService.updateData(requestData);
            httpStatus = OK;
            httpMessage = SUCCESS_UPDATE_MESSAGE;
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
        }
        return response(httpStatus, httpMessage);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<HttpResponse<MessageConfiguration>> deleteISOConfiguration(@PathVariable("id") Long dataId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            messageConfigurationService.deleteData(dataId);
            httpStatus = OK;
            httpMessage = SUCCESS_DELETE_MESSAGE(1);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = Objects.requireNonNull(e.getRootCause()).getMessage();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = removeUnusefulExceptionString(errorMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
        }
        return response(httpStatus, httpMessage);
    }

    @PostMapping("iso8583/search")
    public ResponseEntity<HttpResponse<List<MessageConfiguration>>> searchISOConfiguration(@RequestBody Map<String, Object> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<MessageConfiguration> fetchedData = messageConfigurationService.searchConfiguration(reqBody);
            httpStatus = OK;
            httpMessage = SUCCESS_FETCH_MESSAGE;
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new LinkedList<>());
        }
    }

    @PostMapping("json/search")
    public ResponseEntity<HttpResponse<List<MessageConfiguration>>> searchJSONConfiguration(@RequestBody Map<String, Object> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<MessageConfiguration> fetchedData = messageConfigurationService.searchConfiguration(reqBody);
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
