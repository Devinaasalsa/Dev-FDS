package hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.SpringLogic;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.SpringLogic.FraudBlackList;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.exception.WhiteListExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.exception.WhiteListNotFoundException;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.HttpResponse;
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
@RequestMapping("/whitelist")
public class FraudWhiteListResource extends ResponseResourceEntity<FraudWhiteList> {
    private FraudWhiteListService whiteListService;

    @Autowired
    public FraudWhiteListResource(FraudWhiteListService whiteListService) {
        this.whiteListService = whiteListService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> listWhiteList() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<FraudWhiteList> whiteLists = whiteListService.listAllWhiteList();
            httpStatus = OK;
            httpMessage = "Fraud Whitelist Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, whiteLists);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("/addWhitelist")
    public ResponseEntity<?> addList(@RequestBody FraudWhiteList fraudWhiteList) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            FraudWhiteList addWhiteList = whiteListService.addWhiteList(fraudWhiteList.getEntityType(), fraudWhiteList.getValue(),
                    fraudWhiteList.getUserGroup().getId(), fraudWhiteList.getDateIn(),
                    fraudWhiteList.getDateOut(), fraudWhiteList.getInitiator().getId(),
                    fraudWhiteList.getReason());
            httpStatus = OK;
            httpMessage = "Fraud Whitelist Add Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/updateWhitelist")
    public ResponseEntity<?> updateBlackList(@RequestParam("currentId") Long currentId,
                                             @RequestBody FraudWhiteList fraudWhiteList) throws WhiteListExistException, WhiteListNotFoundException {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            FraudWhiteList updateWhiteList = whiteListService.updateWhiteList(currentId, fraudWhiteList.getEntityType(), fraudWhiteList.getDateIn(), fraudWhiteList.getDateOut(), fraudWhiteList.getValue(), fraudWhiteList.getUserGroup().getId(), fraudWhiteList.getInitiator().getId(), fraudWhiteList.getReason());
            httpStatus = OK;
            httpMessage = "Fraud Whitelist Updated Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("/deleteWhitelist/{id}")
    public ResponseEntity<?> deleteWhiteList(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            whiteListService.deleteWhiteListValue(id);
            httpStatus = OK;
            httpMessage = "Fraud Whitelist Deleted Successfully";
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
            whiteListService.saveWhiteListToDatabase(file, initiatorId, uGroupId);
            httpStatus = OK;
            httpMessage = "Fraud Whitelist data uploaded and saved to database successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchWhiteList(@RequestBody Map<String, Object> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<FraudWhiteList> whiteLists = whiteListService.searchWhiteList(reqBody);
            httpStatus = OK;
            httpMessage = "Fraud Whitelist Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, whiteLists);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
