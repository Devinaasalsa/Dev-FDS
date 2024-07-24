package hbm.fraudDetectionSystem.ApplicationParameters.SpringLogic.AppParameters;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.exception.BlackListExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.exception.BlackListNotFoundException;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/applicationParameters")
public class ApplicationParametersResource extends ResponseResourceEntity<ApplicationParameters> {
    @Autowired
    private ApplicationParametersService parametersService;

    @GetMapping("/list")
    public ResponseEntity<?> listAllAppParameters(){
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<ApplicationParameters> applicationParameters = parametersService.listAllApplicationParameters();
            if (applicationParameters.size() != 0) {
                httpStatus = OK;
                httpMessage = "List of Parameters Fetched Successfully";
            } else {
                httpStatus = NOT_FOUND;
                httpMessage = "Data not found";
            }
            return responseWithListData(httpStatus, httpMessage, applicationParameters);
        } catch (Exception e){
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
    @PostMapping("/addParameters")
    public ResponseEntity<?>addParameters(@RequestBody ApplicationParameters applicationParameters){
        HttpStatus httpStatus;
        String httpMessage;
        try {
            ApplicationParameters addParameters = parametersService.addParameters(applicationParameters.getParamName(), applicationParameters.getValue(),applicationParameters.getDescription());
            httpStatus = OK;
            httpMessage = "add Parameters Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e){
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }
    @PostMapping("/updateParameters")
    public ResponseEntity<?>updateParameters(@RequestParam("currentId") Long currentId,
                                            @RequestBody ApplicationParameters applicationParameters) throws BlackListNotFoundException, BlackListExistException {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            ApplicationParameters updateParameters = parametersService.updateParameters(currentId,applicationParameters.getParamName(), applicationParameters.getValue(), applicationParameters.getDescription());
            httpStatus = OK;
            httpMessage = "Update parameters Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e){
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("/deleteParameters/{id}")
    public ResponseEntity<?>deleteBlackList(@PathVariable("id")long id){
        HttpStatus httpStatus;
        String httpMessage;
        try{
            parametersService.deleteParameters(id);
            httpStatus = OK;
            httpMessage = "Fraud Blacklist Deleted Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e){
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

}
