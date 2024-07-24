package hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListValue;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.FraudListExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.FraudValueExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.FraudValueNotFound;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/fraudListValue")
public class FraudValueResource extends ResponseResourceEntity<FraudValue> {
    private FraudValueService valueService;

    @Autowired
    public FraudValueResource(FraudValueService valueService) {
        this.valueService = valueService;
    }

    @GetMapping("/listFraudValueById")
    public ResponseEntity<?> findAllBydListId(@RequestParam("listId") long listId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<FraudValue> valueList = valueService.findAllByListId(listId);
            httpStatus = OK;
            httpMessage = "Fraudlist Value Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, valueList);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("/addValue")
    public ResponseEntity<?> addListValue(@RequestBody FraudValue listValue) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            FraudValue addValue = valueService.addValue(listValue.getValue(), listValue.getAuthor(), listValue.getListId().getListId());
            httpStatus = OK;
            httpMessage = "Fraud Value Add Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/updateValue")
    public ResponseEntity<?> updateListValue(@RequestParam("currentValue") String currentValue,
                                             @RequestBody FraudValue listValue) throws FraudListExistException, FraudValueNotFound, FraudValueExistException {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            FraudValue updateValue = valueService.updateValue(listValue);
            httpStatus = OK;
            httpMessage = "Fraud Value Updated Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("/deleteValue/{id}")
    public ResponseEntity<?> deleteValue(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            valueService.deleteFraudListValue(id);
            httpStatus = OK;
            httpMessage = "Fraud Value Deleted Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFraudListData(
            @RequestParam("listId") long listId,
            @RequestParam("author") String author,
            @RequestParam("file") MultipartFile file
    ) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            valueService.saveFraudValueToDatabase(listId, author, file);
            httpStatus = OK;
            httpMessage = "FraudValue data uploaded and saved to database successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }
}
