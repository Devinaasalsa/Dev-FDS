package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Role;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.constant.UserConstant.USER_FETCHED_SUCCESSFULLY;
import static hbm.fraudDetectionSystem.GeneralComponent.Constant.ResponseResourceMessage.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/role")
public class RoleResource extends ResponseResourceEntity<Role> {
    private final RoleService roleService;

    public RoleResource(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addRole(@RequestBody Role role) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            roleService.addRole(role.getRoleName(), role.getOperations());
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
    public ResponseEntity<?> getRoles() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<Role> fetchedData = roleService.getRoles();
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
    public ResponseEntity<?> updateRole(@RequestParam("currentRoleName") String currentRoleName,
                                           @RequestBody Role role) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            roleService.updateRole(currentRoleName, role.getRoleName(), role.getOperations());
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
    public ResponseEntity<?> deleteRole(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            roleService.deleteRole(id);
            httpStatus = OK;
            httpMessage = SUCCESS_DELETE_MESSAGE(1);
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @GetMapping("/find/{roleName}")
    public ResponseEntity<?> getRole(@PathVariable("roleName") String roleName) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            Role fetchedData = roleService.findRoleByRoleName(roleName);
            httpStatus = OK;
            httpMessage = SUCCESS_FETCH_MESSAGE;
            return responseWithData(httpStatus, httpMessage, fetchedData);
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
            List<Role> fetchedData = roleService.search(reqBody);
            httpStatus = OK;
            httpMessage = USER_FETCHED_SUCCESSFULLY;

            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
