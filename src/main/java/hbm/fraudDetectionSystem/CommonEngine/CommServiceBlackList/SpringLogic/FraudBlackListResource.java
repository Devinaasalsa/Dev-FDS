package hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.SpringLogic;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.exception.BlackListExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.exception.BlackListNotFoundException;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/Blacklist")
public class FraudBlackListResource extends ResponseResourceEntity<FraudBlackList> {
    private FraudBlackListService blackListService;

    @Autowired
    public FraudBlackListResource(FraudBlackListService blackListService) {
        this.blackListService = blackListService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> listAllBlacklist() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<FraudBlackList> fraudBlackLists = blackListService.listAllBlackList();
            httpStatus = OK;
            httpMessage = "Fraud Blacklist Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fraudBlackLists);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("/addBlacklist")
    public ResponseEntity<?> addList(@RequestBody FraudBlackList fraudBlackList) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            FraudBlackList addBlackList = blackListService.addBlackList(fraudBlackList.getEntityType(), fraudBlackList.getValue(),
                    fraudBlackList.getUserGroup().getId(), fraudBlackList.getDateIn(),
                    fraudBlackList.getDateOut(), fraudBlackList.getInitiator().getId(), fraudBlackList.getReason());
            httpStatus = OK;
            httpMessage = "Fraud Blacklist Add Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/updateBlacklist")
    public ResponseEntity<?> updateBlackList(@RequestParam("currentId") Long currentId,
                                             @RequestBody FraudBlackList fraudBlackList) throws BlackListNotFoundException, BlackListExistException {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            FraudBlackList updateBlackList = blackListService.updateBlackList(currentId, fraudBlackList.getEntityType(), fraudBlackList.getDateIn(), fraudBlackList.getDateOut(), fraudBlackList.getValue(), fraudBlackList.getUserGroup().getId(), fraudBlackList.getInitiator().getId(), fraudBlackList.getReason());
            httpStatus = OK;
            httpMessage = "Fraud Blacklist Update Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("/deleteBlacklist/{id}")
    public ResponseEntity<?> deleteBlackList(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            blackListService.deleteBlackListValue(id);
            httpStatus = OK;
            httpMessage = "Fraud Blacklist Deleted Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadBlackListData(
            @RequestParam("file") MultipartFile file,
            @RequestParam("initiatorId") long initiatorId,
            @RequestParam("uGroupId") long uGroupId
    ) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            blackListService.saveBlackListToDatabase(file, initiatorId, uGroupId);
            httpStatus = OK;
            httpMessage = "Fraud Blacklist data uploaded and saved to database successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchBlackList(@RequestBody Map<String, Object> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<FraudBlackList> fraudBlackLists = blackListService.searchFraudBlackList(reqBody);
            httpStatus = OK;
            httpMessage = "Fraud Blacklist Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fraudBlackLists);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
