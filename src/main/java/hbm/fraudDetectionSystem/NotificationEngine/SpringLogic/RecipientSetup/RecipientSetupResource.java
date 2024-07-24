package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.RecipientSetup;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/recipientSetup")
public class RecipientSetupResource extends ResponseResourceEntity<RecipientSetup> {
    private RecipientSetupService setupService;

    @Autowired
    public RecipientSetupResource(RecipientSetupService setupService) {
        this.setupService = setupService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> listAllRecipientSetup(){
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<RecipientSetup> fetchedData = setupService.findAllRecipientSetup();
                httpStatus = OK;
                httpMessage = "Recipient Setup Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @GetMapping("/listSetupByGroupId")
    public ResponseEntity<?> findAllBydListId(@RequestParam("groupId") long groupId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<RecipientSetup> setupList = setupService.findAllByGroupId(groupId);
            httpStatus = OK;
            httpMessage = "Setup Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, setupList);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("/addRecipientSetup")
    public ResponseEntity<?> addRecipientSetup(@RequestBody RecipientSetup recipientSetup) throws Exception {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            setupService.addRecipientSetup(recipientSetup);
            httpStatus = OK;
            httpMessage = "Recipient Setup Add Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/updateRecipientSetup")
    public  ResponseEntity<?>updateRecipientSetup(@RequestParam("currentId") Long currentId,
                                                 @RequestBody RecipientSetup recipientSetup) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            setupService.updateRecipientSetup(recipientSetup);
            httpStatus = OK;
            httpMessage = "Recipient Setup Update Successfully";
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
            setupService.deleteRecipientId(id);
            httpStatus = OK;
            httpMessage = "Recipient Setup Deleted Successfully";
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
            List<RecipientSetup> fetchedData = setupService.search(reqBody);
            httpStatus = OK;
            httpMessage = "Recipient Setup Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("/addRecipientSetupToGroup")
    public ResponseEntity<?> addRecipientSetupToGroup(@RequestBody Map<String, Long> reqBody) throws Exception {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            setupService.addRecipientSetupToGroup(reqBody);
            httpStatus = OK;
            httpMessage = "Recipient Setup Add Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("/removeRecipientSetupFromGroup/{id}")
    public ResponseEntity<?> removeRecipientSetupFromGroup(@PathVariable("id") long id) throws Exception {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            setupService.removeRecipientSetupFromGroup(id);
            httpStatus = OK;
            httpMessage = "Recipient Setup removed from group Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("import")
    public ResponseEntity<?>importContact(@RequestParam("file") MultipartFile file) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            setupService.importContact(file);
            httpStatus = OK;
            httpMessage = "Contact data uploaded and saved to database successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

}
