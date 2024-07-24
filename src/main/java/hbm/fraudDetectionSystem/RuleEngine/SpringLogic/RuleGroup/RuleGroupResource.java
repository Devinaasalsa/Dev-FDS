package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleGroup;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/ruleGroup")
public class RuleGroupResource extends ResponseResourceEntity<RuleGroup> {
    private RuleGroupService groupService;

    @Autowired
    public RuleGroupResource(RuleGroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> listRulesGroup(@RequestParam("userGroupId") long groupId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<RuleGroup> fetchedData = groupService.listRulesGroup(groupId);
            httpStatus = OK;
            httpMessage = "Rules Group Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @GetMapping("/list/{userGroupId}")
    public ResponseEntity<?> listRulesGroupByUserGroupId(@PathVariable("userGroupId") long userGroupId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<RuleGroup> fetchedData = groupService.fetchRuleGroupByUserGroup(userGroupId);
            httpStatus = OK;
            httpMessage = "Rules Group Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addRulesGroup(@RequestBody RuleGroup ruleGroup) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            groupService.addRuleGroup(ruleGroup);
            httpStatus = OK;
            httpMessage = "Rules Group Add Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateRulesGroup(@RequestBody RuleGroup ruleGroup) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            groupService.updateRuleGroup(ruleGroup);
            httpStatus = OK;
            httpMessage = "Rules Group Updated Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRulesGroup(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            groupService.deleteRulesGroup(id);
            httpStatus = OK;
            httpMessage = "Rules Group Deleted Successfully";
            return response(httpStatus, httpMessage);
        } catch (DataIntegrityViolationException e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = "This Rule Group still having child reference";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchRuleGroup(@RequestBody Map<String, Object> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<RuleGroup> fetchedData = groupService.searchRuleGroup(reqBody);
            httpStatus = OK;
            httpMessage = "Rules Group Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
