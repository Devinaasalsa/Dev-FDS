package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONHeaderConfiguration;

import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint.ChannelEndpoint;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("json_header_configuration")
public class JSONHeaderConfigurationResource extends ResponseResourceEntity<Object> {
    protected final JSONHeaderConfigurationService jsonHeaderConfigurationService;

    @Autowired
    public JSONHeaderConfigurationResource(JSONHeaderConfigurationService jsonHeaderConfigurationService) {
        this.jsonHeaderConfigurationService = jsonHeaderConfigurationService;
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<?> list(@PathVariable("id") Long configId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            Set<ChannelEndpoint> fetchedData = jsonHeaderConfigurationService.findAllFieldConfiguration(configId);
            httpStatus = OK;
            httpMessage = "Fetch JSON Field Config";
            return responseWithDataObject(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new LinkedList<>());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody JSONHeaderConfiguration data) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            jsonHeaderConfigurationService.saveData(data);
            httpStatus = OK;
            httpMessage = "Successfully add data";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody JSONHeaderConfiguration data) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            jsonHeaderConfigurationService.updateData(data);
            httpStatus = OK;
            httpMessage = "Successfully update data";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            jsonHeaderConfigurationService.deleteData(id);
            httpStatus = OK;
            httpMessage = "Successfully delete data";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

//    @PostMapping("/search")
//    public ResponseEntity<?> searchRule(@RequestBody() Map<String, Object> reqBody) {
//        HttpStatus httpStatus;
//        String httpMessage;
//        try {
//            List<JSONHeaderConfiguration> fetchedData = jsonHeaderConfigurationService.search(reqBody);
//            httpStatus = OK;
//            httpMessage = "Fetch JSON Field Config";
//            return responseWithListObjectData(httpStatus, httpMessage, fetchedData);
//        } catch (Exception e) {
//            httpStatus = INTERNAL_SERVER_ERROR;
//            httpMessage = e.getMessage();
//            return responseWithListObjectData(httpStatus, httpMessage, new ArrayList<>());
//        }
//    }
}
