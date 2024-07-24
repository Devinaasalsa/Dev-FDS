package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.SupportedMTI;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.HttpResponse;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static hbm.fraudDetectionSystem.GeneralComponent.Constant.ResponseResourceMessage.*;
import static hbm.fraudDetectionSystem.GeneralComponent.Utility.SpringLogicHelper.removeUnusefulExceptionString;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(path = "/supported_mti")
public class SupportedMTIResource extends ResponseResourceEntity<SupportedMTI> {
    private final SupportedMTIService supportedMTIService;

    @Autowired
    public SupportedMTIResource(SupportedMTIService supportedMTIService) {
        this.supportedMTIService = supportedMTIService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<HttpResponse<List<SupportedMTI>>> findAllSupportedMTI() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<SupportedMTI> fetchedData = supportedMTIService.findAllData();
            if (fetchedData.size() != 0) {
                httpStatus = OK;
                httpMessage = SUCCESS_FETCH_MESSAGE;
            } else {
                httpStatus = NOT_FOUND;
                httpMessage = DATA_NOT_FOUND_MESSAGE;
            }
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new LinkedList<>());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<HttpResponse<SupportedMTI>> addSupportedMTI(@RequestBody SupportedMTI requestData) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            supportedMTIService.saveData(requestData);
            httpStatus = OK;
            httpMessage = SUCCESS_ADD_MESSAGE;
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

    @PostMapping("/update")
    public ResponseEntity<HttpResponse<SupportedMTI>> updateSupportedMTI(@RequestBody SupportedMTI requestData) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            supportedMTIService.updateData(requestData);
            httpStatus = OK;
            httpMessage = SUCCESS_UPDATE_MESSAGE;
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpResponse<SupportedMTI>> deleteSupportedMTI(@PathVariable("id") Long requestData) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            supportedMTIService.deleteData(requestData);
            httpStatus = OK;
            httpMessage = SUCCESS_DELETE_MESSAGE(1);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
        }
        return response(httpStatus, httpMessage);
    }
}
