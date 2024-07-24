package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserType;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.constant.UserConstant.USER_FETCHED_SUCCESSFULLY;
import static hbm.fraudDetectionSystem.GeneralComponent.Constant.ResponseResourceMessage.*;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/type")
public class UserTypeResource extends ResponseResourceEntity<Type> {
    private final UserTypeService userTypeService;

    public UserTypeResource(UserTypeService userTypeService) {
        this.userTypeService = userTypeService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addType(@RequestBody Type type) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            userTypeService.addType(type.getTypeName());
            httpStatus = OK;
            httpMessage = SUCCESS_ADD_MESSAGE;
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getTypes() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<Type> fetchedData = userTypeService.getTypes();
                httpStatus = OK;
                httpMessage = USER_FETCHED_SUCCESSFULLY;
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateType(@RequestParam("currentTypeName") String currentTypeName,
                                               @RequestParam("typeName") String typeName) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            userTypeService.updateType(currentTypeName, typeName);
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
    public ResponseEntity<?> deleteType(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            userTypeService.deleteType(id);
            httpStatus = OK;
            httpMessage = SUCCESS_DELETE_MESSAGE(1);
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @GetMapping("/find/{typeName}")
    public ResponseEntity<?> getType(@PathVariable("typeName") String typeName) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            Type type = userTypeService.findTypeByTypeName(typeName);
            httpStatus = OK;
            httpMessage = SUCCESS_UPDATE_MESSAGE;
            return responseWithData(httpStatus, httpMessage, type);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

}
