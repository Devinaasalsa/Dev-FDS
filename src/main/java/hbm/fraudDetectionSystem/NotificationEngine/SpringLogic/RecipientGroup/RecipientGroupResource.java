package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.RecipientGroup;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/recipientGroup")
public class RecipientGroupResource extends ResponseResourceEntity<RecipientGroup> {
    private RecipientGroupService groupService;

    @Autowired
    public RecipientGroupResource(RecipientGroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> listAllRecipientGroup(){
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<RecipientGroup> fetchedData = groupService.findAllRecipientGroup();
                httpStatus = OK;
                httpMessage = "Recipient Group Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("/addRecipientGroup")
    public ResponseEntity<?> addRecipientGroup(@RequestBody RecipientGroup recipientGroup) throws Exception {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            groupService.addRecipientGroup(recipientGroup.getGroupName(), recipientGroup.getNotificationType().getId(), recipientGroup.getRecipientSetups());
            httpStatus = OK;
            httpMessage = "Recipient Group Add Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/updateRecipientGroup")
    public  ResponseEntity<?>updateRecipientGroup(@RequestParam("currentId") Long currentId,
                                                  @RequestBody RecipientGroup recipientGroup) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            groupService.updateRecipientGroup(currentId, recipientGroup.getGroupName(), recipientGroup.getNotificationType().getId(), recipientGroup.getRecipientSetups());
            httpStatus = OK;
            httpMessage = "Recipient Group Update Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e){
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFiltration(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            groupService.deleteGroupId(id);
            httpStatus = OK;
            httpMessage = "Recipient Group Deleted Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody Map<String, Object> reqBody){
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<RecipientGroup> fetchedData = groupService.search(reqBody);
            httpStatus = OK;
            httpMessage = "Recipient Group Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
