package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/rule")
public class RuleResource extends ResponseResourceEntity<Rule> {
    private RuleService ruleService;

    @Autowired
    public RuleResource(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> listAllRulesByUserGroupId(@RequestParam("userGroupId") long groupId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<Rule> fetchedData = ruleService.fetchRulesByUserGroup(groupId);
            httpStatus = OK;
            httpMessage = "Rules Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @GetMapping("/listByGroupId")
    public ResponseEntity<?> listAllRulesByRuleGroupId(@RequestParam("groupId") long groupId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<Rule> fetchedData = ruleService.fetchRulesByRuleGroup(groupId);
            httpStatus = OK;
            httpMessage = "Rules Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @GetMapping("/findRuleByRuleId")
    public ResponseEntity<?> findRulesByRuleId(@RequestParam("ruleId") long ruleId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            Rule findRuleByRuleId = ruleService.findRuleById(ruleId);
            httpStatus = OK;
            httpMessage = "Rule Add Successfully";
            return responseWithData(httpStatus, httpMessage, findRuleByRuleId);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addRules(@RequestBody Rule aRule) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            ruleService.add(aRule);
            httpStatus = OK;
            httpMessage = "Rule Add Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateRules(@RequestBody Rule aRule) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            ruleService.update(aRule);
            httpStatus = OK;
            httpMessage = "Rule Update Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRules(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            ruleService.delete(id);
            httpStatus = OK;
            httpMessage = "Rule Deleted Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/activation")
    public ResponseEntity<?> activation(@RequestParam long ruleId, @RequestParam String initiator, @RequestParam String comment) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            ruleService.activation(ruleId, initiator, comment);
            httpStatus = OK;
            httpMessage = "Activation successfully submitted, waiting for approval";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/deactivation")
    public ResponseEntity<?> deactivation(@RequestParam long ruleId, @RequestParam String initiator, @RequestParam String comment) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            ruleService.deactivation(ruleId, initiator, comment);
            httpStatus = OK;
            httpMessage = "Deactivation successfully submitted, waiting for approval";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/approval")
    public ResponseEntity<?> approval(@RequestParam long ruleId, @RequestParam String initiator, @RequestParam String comment) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            ruleService.approval(ruleId, initiator, comment);
            httpStatus = OK;
            httpMessage = "Request has been approved";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/rejection")
    public ResponseEntity<?> rejection(@RequestParam long ruleId, @RequestParam String initiator, @RequestParam String comment) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            ruleService.rejection(ruleId, initiator, comment);
            httpStatus = OK;
            httpMessage = "Request has been rejected";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("export")
    public ResponseEntity<?> exportRuleId(@RequestBody List<Long> requestBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {

            String timestamp = LocalDateTime.now().toString().replace(":", "-");
            String filename = "temp-" + timestamp + ".txt";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(ruleService.export(requestBody));
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file")MultipartFile file) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }
            ruleService.importRule(file);
            httpStatus = OK;
            httpMessage = "Import Success";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchRule(@RequestBody() Map<String, Object> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<Rule> fetchedData = ruleService.searchRule(reqBody);
            httpStatus = OK;
            httpMessage = "Rules Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
