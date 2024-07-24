package hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.SpringLogic.Case;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/caseManagement")
public class CaseResource extends ResponseResourceEntity<Object> {
    private CaseService caseService;

    @Autowired
    public CaseResource(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> listAllCases() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<Map<Object, Object>> cases = caseService.findAllCase();
            httpStatus = OK;
            httpMessage = "Case Fetched Successfully";
            return responseWithDataObject(httpStatus, httpMessage, cases);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @GetMapping("/list/{lockedBy}")
    public ResponseEntity<?> listAllCases(@PathVariable("lockedBy") String lockedBy) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<Map<Object, Object>> cases = caseService.findCaseOrderByLockedByAndCaseId(lockedBy);
            httpStatus = OK;
            httpMessage = "Case Fetched Successfully";
            return responseWithDataObject(httpStatus, httpMessage, cases);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("/lockCase")
    public ResponseEntity<?> lockCase(@RequestBody Case aCase) throws Exception {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            Case lockCase = caseService.lockCase(aCase.getCaseId(), aCase.getLockedBy());
            httpStatus = OK;
            httpMessage = "Case lock Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/unlockCase")
    public ResponseEntity<?> unlockCase(@RequestBody Case aCase) throws Exception {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            Case unlockCase = caseService.unlockCase(aCase.getCaseId(), aCase.getLockedBy());
            httpStatus = OK;
            httpMessage = "Case unlock Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/clasify")
    public ResponseEntity<?> clasifyCase(@RequestBody Case aCase) throws Exception {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            Case clasifyCase = caseService.clasifyAlert(aCase.getCaseId(), aCase.getInitiator(), aCase.getClasificationType(), aCase.getClasifiedComment());
            httpStatus = OK;
            httpMessage = "Case clasify Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/takeAction")
    public ResponseEntity<?> takeAction(@RequestBody Case aCase) throws Exception {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            Case addAlertComment = caseService.takeAction(aCase.getCaseId(), aCase.getInitiator(), aCase.getActionType(),
                    aCase.getCaseComment(), aCase.getListId(),
                    aCase.getValue(), aCase.getReason(), aCase.getEntityType(), aCase.getDatein(),
                    aCase.getDateout(), aCase.getUserGroupId());
            httpStatus = OK;
            httpMessage = "Take Action Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/takeAction/forwardedTo")
    public ResponseEntity<?> takeActionForwardedTo(@RequestBody Case aCase) throws Exception {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            caseService.takeActionForwardedTo(aCase.getCaseId(), aCase.getInitiator(), aCase.getActionType(), aCase.getCaseComment(), aCase.getForwardedTo(), aCase.getListId(), aCase.getValue()
                    , aCase.getReason(), aCase.getEntityType(), aCase.getDatein(), aCase.getDateout(), aCase.getUserGroupId());
            httpStatus = OK;
            httpMessage = "Add ForwardedTo Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @GetMapping("/{caseId}/caseId")
    public ResponseEntity<?> findByCaseId(@PathVariable("caseId") long caseId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            Case aCase = caseService.findByCaseId(caseId);
            httpStatus = OK;
            httpMessage = "Find By Case Id " + caseId;
            return responseWithData(httpStatus, httpMessage, aCase);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<?> getUsername(@PathVariable("username") String username) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            Map<String, Object> cases = caseService.findCaseByUsername(username);
            httpStatus = OK;
            httpMessage = "Find by Username";
            return responseWithData(httpStatus, httpMessage, cases);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<?> getUsername(@RequestBody Map<String, Object> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<Map<String, Object>> cases = caseService.searchAlert(reqBody);
            httpStatus = OK;
            httpMessage = "Search Case";
            return responseWithData(httpStatus, httpMessage, cases);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }
}
