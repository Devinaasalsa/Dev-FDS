package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.MappingInfoTransaction;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/infoTrans")
public class FraudInfoTransResource  extends ResponseResourceEntity<FraudInfoTransaction> {
    private FraudInfoTransService infoTransService;

    @Autowired
    public FraudInfoTransResource(FraudInfoTransService infoTransService) {
        this.infoTransService = infoTransService;
    }

    @GetMapping("/list")
    public ResponseEntity<?>getAllInfoTrans(@RequestParam("utrnno")String utrnno, @RequestParam("refnum")String refnum){
        HttpStatus httpStatus;
        String httpMessage;
        try {
            FraudInfoTransaction getAllInfo = infoTransService.findTransInfoByEntity(utrnno, refnum);
            httpStatus = OK;
            httpMessage = "Data Fetched Successfully";
            return responseWithData(httpStatus, httpMessage, getAllInfo);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithData(httpStatus, httpMessage, null);
        }
    }

}
