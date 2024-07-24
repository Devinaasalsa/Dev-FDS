package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/transaction")
public class CurrTransResource extends ResponseResourceEntity<Object> {
    private CurrTransService transService;

    @Autowired
    public CurrTransResource(CurrTransService transService) {
        this.transService = transService;
    }

    @GetMapping("/list")
    public ResponseEntity<?>listTransactions(){
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<CurrTrans> fetchedData = transService.findAllTransaction();
                httpStatus = OK;
                httpMessage = "Transaction Fetched Successfully";
            return responseWithListObjectData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("/assignFlag")
    public ResponseEntity<?>assignFlag(@RequestBody CurrTrans currTrans){
        HttpStatus httpStatus;
        String httpMessage;
        try {
            CurrTrans assignFlag = transService.assignFraudFlag(currTrans.getUtrnno(), currTrans.getFraudFlags());
            httpStatus = OK;
            httpMessage = "Assign Flag Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @GetMapping("/find/hpan/{hpan}")
    public ResponseEntity<?> getHpan(@PathVariable("hpan") String hpan) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<CurrTrans> currTrans = transService.findAllByHpan(hpan);
            httpStatus = OK;
            httpMessage = "Find by Hpan";
            return responseWithListObjectData(httpStatus, httpMessage, currTrans);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/findByHpanCustAcct")
    public ResponseEntity<?> getCustId(@RequestBody Map<String, String> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<CurrTrans> currTrans = transService.findAllByHpanCustIdAcct1(
                    reqBody.get("hpan"),
                    reqBody.get("custId"),
                    reqBody.get("acct1"),
                    reqBody.get("alertDate")
            );
            httpStatus = OK;
            httpMessage = "Find by Hpan Cust Acct1";
            return responseWithListObjectData(httpStatus, httpMessage, currTrans);
        } catch (Exception e) {
            e.printStackTrace();
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
            List<CurrTrans> currTrans = transService.searchTransaction(reqBody);
            httpStatus = OK;
            httpMessage = "Search Transaction";
            return responseWithListObjectData(httpStatus, httpMessage, currTrans);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }
}
