package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Operation;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Role.Role;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.constant.UserConstant.USER_FETCHED_SUCCESSFULLY;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/operation")
public class OperationResource extends ResponseResourceEntity<Operation> {
    private final OperationService operationService;

    @Autowired
    public OperationResource(OperationService operationService) {
        this.operationService = operationService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> getRoles() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<Operation> fetchedData = operationService.findAllOperations();
            httpStatus = OK;
            httpMessage = USER_FETCHED_SUCCESSFULLY;

            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }
}
