package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.TemplateSetup;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/templateSetup")
public class TemplateSetupResource extends ResponseResourceEntity<TemplateSetup> {
    private TemplateSetupService setupService;

    @Autowired
    public TemplateSetupResource(TemplateSetupService setupService) {
        this.setupService = setupService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> listAllTemplate() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<TemplateSetup> fetchedData = setupService.findAllTemplateSetup();
            httpStatus = OK;
            httpMessage = "Template Setup Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("/addTemplateSetup")
    public ResponseEntity<?> addTemplateSetup(@RequestBody TemplateSetup templateSetup) throws Exception {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            setupService.addTemplateSetup(templateSetup.getTemplateText(), templateSetup.getDescription(), templateSetup.getSubjectText(), templateSetup.getNotificationType().getId());
            httpStatus = OK;
            httpMessage = "Template Setup Add Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/updateTemplateSetup")
    public ResponseEntity<?> updateTemplateSetup(@RequestParam("currentId") Long currentId,
                                                 @RequestBody TemplateSetup templateSetup) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            setupService.updateTemplateSetup(currentId, templateSetup.getTemplateText(), templateSetup.getDescription(), templateSetup.getSubjectText(), templateSetup.getNotificationType().getId());
            httpStatus = OK;
            httpMessage = "Template Setup Update Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
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
            setupService.deleteTemplateId(id);
            httpStatus = OK;
            httpMessage = "Template Setup Deleted Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody Map<String, Object> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<TemplateSetup> fetchedData = setupService.search(reqBody);
            httpStatus = OK;
            httpMessage = "Template Setup Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
