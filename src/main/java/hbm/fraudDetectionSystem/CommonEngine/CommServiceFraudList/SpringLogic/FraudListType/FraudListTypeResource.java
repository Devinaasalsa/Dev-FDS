package hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListType;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudList.FraudList;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/fraudListType")
public class FraudListTypeResource extends ResponseResourceEntity<FraudListType> {
    private FraudListTypeService typeService;

    @Autowired
    public FraudListTypeResource(FraudListTypeService typeService) {
        this.typeService = typeService;
    }

    @GetMapping("/list")
    public ResponseEntity<?>listTypeFraud(){
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<FraudListType>getAllList = typeService.listAllEntityType();
                httpStatus = OK;
                httpMessage = "Fraudlist Type Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, getAllList);
        } catch (Exception e){
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

}
