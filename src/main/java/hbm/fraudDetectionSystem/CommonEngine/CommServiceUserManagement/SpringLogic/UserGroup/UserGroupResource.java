package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.UserGroupExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.UserGroupNotFoundException;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.constant.UserConstant.USER_FETCHED_SUCCESSFULLY;
import static hbm.fraudDetectionSystem.GeneralComponent.Constant.ResponseResourceMessage.*;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/user/group")
public class UserGroupResource extends ResponseResourceEntity<UserGroup> {
    private final UserGroupService userGroupService;

    public UserGroupResource(UserGroupService userGroupService) {
        this.userGroupService = userGroupService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUserGroup(@RequestBody UserGroup userGroup) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            userGroupService.addUserGroup(userGroup.getGroupName());
            httpStatus = OK;
            httpMessage = SUCCESS_ADD_MESSAGE;
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @GetMapping("list")
    public ResponseEntity<?> getUserGroups() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<UserGroup> fetchedData = userGroupService.getUserGroups();
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
    public ResponseEntity<?> updateUserGroup(@RequestParam("currentGroupName") String currentGroupName,
                                                     @RequestParam("groupName") String groupName) throws UserGroupNotFoundException, UserGroupExistException {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            userGroupService.updateUserGroup(currentGroupName, groupName);
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
    public ResponseEntity<?> deleteUserGroup(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            userGroupService.deleteUserGroup(id);
            httpStatus = OK;
            httpMessage = SUCCESS_DELETE_MESSAGE(1);
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @GetMapping("/find/{groupName}")
    public ResponseEntity<?> getUserGroups(@PathVariable("groupName") String groupName) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            UserGroup userGroup = userGroupService.findUserGroupByGroupName(groupName);
            httpStatus = OK;
            httpMessage = SUCCESS_FETCH_MESSAGE;
            return responseWithData(httpStatus, httpMessage, userGroup);
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
            List<UserGroup> fetchedData = userGroupService.search(reqBody);
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
