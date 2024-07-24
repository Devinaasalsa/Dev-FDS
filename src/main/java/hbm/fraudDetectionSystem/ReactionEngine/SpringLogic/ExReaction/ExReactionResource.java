package hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.ExReaction;


import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/ex_reactions")
public class ExReactionResource extends ResponseResourceEntity<ReactionContainer> {
    protected final ExReactionService exReactionService;

    @Autowired
    public ExReactionResource(ExReactionService exReactionService) {
        this.exReactionService = exReactionService;
    }

    @GetMapping("/findByUtrnno")
    public ResponseEntity<?> findByUtrnno(@RequestParam("utrnno") long utrnno) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<ReactionContainer> fetchedData = exReactionService.fetchExReactionsByUtrnno(utrnno);
            httpStatus = OK;
            httpMessage = "Ex Reactions Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
