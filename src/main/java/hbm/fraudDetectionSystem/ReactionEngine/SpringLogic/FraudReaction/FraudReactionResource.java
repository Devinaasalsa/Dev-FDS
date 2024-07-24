package hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.FraudReaction;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/fraudReaction")
public class FraudReactionResource extends ResponseResourceEntity<FraudReaction> {
    private FraudReactionService reactionsService;

    @Autowired
    public FraudReactionResource(FraudReactionService reactionsService) {
        this.reactionsService = reactionsService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> listFraudReactions() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<FraudReaction> fraudReactionList = reactionsService.listFraudReactions();
            httpStatus = OK;
            httpMessage = "Fraud Reaction Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fraudReactionList);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @GetMapping("/listByBindingIdAndBindingType")
    public ResponseEntity<?> listReactionByBindingId(@RequestParam("bindingId") long bindingId, @RequestParam("bindingType") String bindingType) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<FraudReaction> fraudReactionList = reactionsService.findReactionByBindingTypeAndBindingId(bindingType, bindingId);
            httpStatus = OK;
            httpMessage = "Fraud Reaction Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fraudReactionList);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addFraudReactions(@RequestBody FraudReaction fraudReaction) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            reactionsService.add(fraudReaction);
            httpStatus = OK;
            httpMessage = "Fraud Reaction Add Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateFraudReactions(@RequestBody FraudReaction fraudReaction) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            reactionsService.update(fraudReaction);
            httpStatus = OK;
            httpMessage = "Fraud Reaction Updated Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFraudReactions(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            reactionsService.deleteFraudReactions(id);
            httpStatus = OK;
            httpMessage = "Fraud Reaction Deleted Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchFraudReactions(@RequestBody Map<String, Object> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<FraudReaction> fraudReactionList = this.reactionsService.searchReaction(reqBody);
            httpStatus = OK;
            httpMessage = "Search Fraud Reaction Successfully";
            return responseWithListData(httpStatus, httpMessage, fraudReactionList);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
