package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransactionStatus;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

import static hbm.fraudDetectionSystem.GeneralComponent.Constant.ResponseResourceMessage.SUCCESS_FETCH_MESSAGE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("transaction_status")
public class TransactionStatusResource extends ResponseResourceEntity<TransactionStatus> {
    protected final TransactionStatusService transactionStatusService;

    @Autowired
    public TransactionStatusResource(TransactionStatusService transactionStatusService) {
        this.transactionStatusService = transactionStatusService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> fetchAllData(@RequestParam("dateType") int dateType) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<TransactionStatus> fetchedData = transactionStatusService.findAllByDateType(dateType);
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
