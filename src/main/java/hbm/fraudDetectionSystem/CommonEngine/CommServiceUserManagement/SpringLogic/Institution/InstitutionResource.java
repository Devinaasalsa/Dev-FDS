package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Institution;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.InstitutionExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.InstitutionNotFoundException;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.constant.UserConstant.USER_FETCHED_SUCCESSFULLY;
import static hbm.fraudDetectionSystem.GeneralComponent.Constant.ResponseResourceMessage.*;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/institution")
public class InstitutionResource extends ResponseResourceEntity<Institution> {
    private final InstitutionService institutionService;

    public InstitutionResource(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addInstitution(@RequestBody Institution institution) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            institutionService.addInstitution(institution.getInstitutionName(), institution.getDescription());
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
    public ResponseEntity<?> getInstitutions() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<Institution> fetchedData = institutionService.getInstitutions();
                httpStatus = OK;
                httpMessage = USER_FETCHED_SUCCESSFULLY;
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateInstitution(@RequestParam("currentInstitutionName") String currentInstitutionName,
                                                         @RequestParam("institutionName") String institutionName,
                                                         @RequestParam("description") String description) throws InstitutionNotFoundException, InstitutionExistException {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            institutionService.updateInstitution(currentInstitutionName, institutionName, description);
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
    public ResponseEntity<?> deleteInstitution(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            institutionService.deleteInstitution(id);
            httpStatus = OK;
            httpMessage = SUCCESS_DELETE_MESSAGE(1);
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @GetMapping("/find/{institutionName}")
    public ResponseEntity<?> getInstitution(@PathVariable("institutionName") String institutionName) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            Institution institution = institutionService.findInstitutionByInstitutionName(institutionName);
            httpStatus = OK;
            httpMessage = SUCCESS_FETCH_MESSAGE;
            return responseWithData(httpStatus, httpMessage, institution);
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
            List<Institution> institutions = institutionService.search(reqBody);
            httpStatus = OK;
            httpMessage = SUCCESS_ADD_MESSAGE;
            return responseWithListData(httpStatus, httpMessage, institutions);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
