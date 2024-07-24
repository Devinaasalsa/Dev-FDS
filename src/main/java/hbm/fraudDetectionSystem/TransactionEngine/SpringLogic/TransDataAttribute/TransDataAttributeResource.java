package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransDataAttribute;


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
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(path = "/transDataAttr")
public class TransDataAttributeResource extends ResponseResourceEntity<TransDataAttribute> {
    protected final TransDataAttributeService transDataAttributeService;

    @Autowired
    public TransDataAttributeResource(TransDataAttributeService transDataAttributeService) {
        this.transDataAttributeService = transDataAttributeService;
    }

    @GetMapping("/list")
    public ResponseEntity<HttpResponse<List<TransDataAttribute>>> fetchAllData() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<TransDataAttribute> fetchedData = transDataAttributeService.fetchAllAttr();
                httpStatus = OK;
                httpMessage = SUCCESS_FETCH_MESSAGE;
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new LinkedList<>());
        }
    }

    @GetMapping("/list/common")
    public ResponseEntity<HttpResponse<List<TransDataAttribute>>> fetchAllDataCommon() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<TransDataAttribute> fetchedData = transDataAttributeService.fetchAllAttrCommon();
            httpStatus = OK;
            httpMessage = SUCCESS_FETCH_MESSAGE;
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new LinkedList<>());
        }
    }

    @GetMapping("/list/{configId}")
    public ResponseEntity<?> fetchByConfigId(@PathVariable("configId") long configId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<TransDataAttribute> fetchedData = transDataAttributeService.fetchAllByConfigIdUI(configId);
            httpStatus = OK;
            httpMessage = SUCCESS_FETCH_MESSAGE;
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new LinkedList<>());
        }
    }

    @GetMapping("/listByEndpoint/{id}")
    public ResponseEntity<?> fetchByConfigIdAndEndpointId(@PathVariable("id") long endpointId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<TransDataAttribute> fetchedData = transDataAttributeService.fetchAllByEndpointIdUI(
                    endpointId
            );
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
    public ResponseEntity<HttpResponse<TransDataAttribute>> addData(@RequestBody TransDataAttribute requestData) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            transDataAttributeService.saveData(requestData);
            httpStatus = OK;
            httpMessage = SUCCESS_ADD_MESSAGE;
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            e.printStackTrace();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<HttpResponse<TransDataAttribute>> updateData(@RequestBody TransDataAttribute requestData) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            transDataAttributeService.updateData(requestData);

                httpStatus = OK;
                httpMessage = SUCCESS_UPDATE_MESSAGE;
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpResponse<TransDataAttribute>> deleteData(@PathVariable("id") Long requestData) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            transDataAttributeService.deleteData(requestData);
            httpStatus = OK;
            httpMessage = SUCCESS_DELETE_MESSAGE(1);
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody Map<String, Object> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<TransDataAttribute> fetchData = transDataAttributeService.search(reqBody);

            httpStatus = OK;
            httpMessage = SUCCESS_UPDATE_MESSAGE;
            return responseWithListData(httpStatus, httpMessage, fetchData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
