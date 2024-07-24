package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserAudit;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.HttpResponse;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static hbm.fraudDetectionSystem.GeneralComponent.Constant.ResponseResourceMessage.DATA_NOT_FOUND_MESSAGE;
import static hbm.fraudDetectionSystem.GeneralComponent.Constant.ResponseResourceMessage.SUCCESS_FETCH_MESSAGE;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/user/audit")
public class UserAuditResource extends ResponseResourceEntity<UserAudit> {
    protected final UserAuditService userAuditService;

    @Autowired
    public UserAuditResource(UserAuditService userAuditService) {
        this.userAuditService = userAuditService;
    }

    @GetMapping("list")
    public ResponseEntity<HttpResponse<List<UserAudit>>> fetchAllUserAudit() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<UserAudit> fetchedData = userAuditService.fetchAllAudit();
                httpStatus = OK;
                httpMessage = SUCCESS_FETCH_MESSAGE;
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new LinkedList<>());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse<List<UserAudit>>> fetchAllUserAudit(@PathVariable("id") Long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<UserAudit> fetchedData = userAuditService.fetchAuditByUserId(id);
            if (fetchedData.size() != 0) {
                httpStatus = OK;
                httpMessage = SUCCESS_FETCH_MESSAGE;
            } else {
                httpStatus = NOT_FOUND;
                httpMessage = DATA_NOT_FOUND_MESSAGE;
            }
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new LinkedList<>());
        }
    }

    @PostMapping("search")
    public ResponseEntity<?> search(@RequestBody Map<String, Object> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<UserAudit> fetchedData = userAuditService.search(reqBody);
            httpStatus = OK;
            httpMessage = SUCCESS_FETCH_MESSAGE;
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new LinkedList<>());
        }
    }
}
