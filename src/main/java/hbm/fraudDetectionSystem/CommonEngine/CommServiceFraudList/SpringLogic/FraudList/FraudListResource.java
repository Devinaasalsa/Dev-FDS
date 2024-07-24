package hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudList;


import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.FraudListExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.ListFraudNameNotFound;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.SpringLogic.FraudWhiteList;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.HttpResponse;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/sanctionList")
public class FraudListResource extends ResponseResourceEntity<FraudList> {
    private FraudListService fraudListService;

    @Autowired
    public FraudListResource(FraudListService fraudListService) {
        this.fraudListService = fraudListService;
    }


    @GetMapping("/list")
    public ResponseEntity<?> getAllFraudlist() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<FraudList> fraudLists = fraudListService.getSanctionList();
            httpStatus = OK;
            httpMessage = "Fraudlist Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fraudLists);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @GetMapping("/listByEntityType")
    public ResponseEntity<?> getAllFraudlistByEntityType(@RequestParam("entityTypeId") int entityTypeId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<FraudList> fraudLists = fraudListService.getListByEntityType(entityTypeId);
            httpStatus = OK;
            httpMessage = "Fraudlist Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fraudLists);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("/addList")
    public ResponseEntity<?> addList(@RequestBody FraudList fraudList) throws ListFraudNameNotFound, FraudListExistException {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            FraudList list = fraudListService.addList(fraudList);
            httpStatus = OK;
            httpMessage = "Fraud List Add Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/updateList")
    public ResponseEntity<?> updateSanctionList(@RequestParam("currentListName") String currentListName,
                                                @RequestBody FraudList fraudList) throws ListFraudNameNotFound, FraudListExistException {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            FraudList updateList = fraudListService.updateList(currentListName, fraudList);
            httpStatus = OK;
            httpMessage = "Fraud List Updated Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("/deleteList/{listId}")
    public ResponseEntity<?> deleteList(@PathVariable("listId") long listId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            fraudListService.deleteSanctionList(listId);
            httpStatus = OK;
            httpMessage = "Fraud List Deleted Successfully";
            return response(httpStatus, httpMessage);
        } catch (DataIntegrityViolationException e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = "This Fraud List still having child reference";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchFraudList(@RequestBody Map<String, Object> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<FraudList> fraudLists = fraudListService.searchFraudList(reqBody);
            httpStatus = OK;
            httpMessage = "Fraudlist Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fraudLists);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
