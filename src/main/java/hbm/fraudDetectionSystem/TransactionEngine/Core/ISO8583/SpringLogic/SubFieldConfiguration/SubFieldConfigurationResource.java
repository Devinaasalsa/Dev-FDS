package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.SubFieldConfiguration;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.HttpResponse;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

import static hbm.fraudDetectionSystem.GeneralComponent.Constant.ResponseResourceMessage.*;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(path = "/iso_subfield_configuration")
public class SubFieldConfigurationResource extends ResponseResourceEntity<SubFieldConfiguration> {
    private final SubFieldConfigurationService subFieldConfigurationService;

    @Autowired
    public SubFieldConfigurationResource(SubFieldConfigurationService subFieldConfigurationService) {
        this.subFieldConfigurationService = subFieldConfigurationService;
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<HttpResponse<List<SubFieldConfiguration>>> findAllISOFieldConfiguration(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<SubFieldConfiguration> fetchedData = subFieldConfigurationService.findByDataId(id);
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
    public ResponseEntity<HttpResponse<SubFieldConfiguration>> addISOFieldConfiguration(@RequestBody SubFieldConfiguration requestData) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            subFieldConfigurationService.saveData(requestData);
            httpStatus = OK;
            httpMessage = SUCCESS_ADD_MESSAGE;
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            e.printStackTrace();
        }
        return response(httpStatus, httpMessage);
    }

    @PostMapping("/update")
    public ResponseEntity<HttpResponse<SubFieldConfiguration>> updateISOFieldConfiguration(@RequestBody SubFieldConfiguration requestData) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            subFieldConfigurationService.updateData(requestData);
            httpStatus = OK;
            httpMessage = SUCCESS_UPDATE_MESSAGE;
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
        }
        return response(httpStatus, httpMessage);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpResponse<SubFieldConfiguration>> deleteISOFieldConfiguration(@PathVariable("id") Long requestData) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            subFieldConfigurationService.deleteData(requestData);
            httpStatus = OK;
            httpMessage = SUCCESS_DELETE_MESSAGE(1);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            e.printStackTrace();
        }
        return response(httpStatus, httpMessage);
    }
}
