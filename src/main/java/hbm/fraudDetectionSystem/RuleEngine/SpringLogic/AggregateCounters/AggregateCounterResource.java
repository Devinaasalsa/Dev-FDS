package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.AggregateCounters;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/aggregateCounters")
public class AggregateCounterResource extends ResponseResourceEntity<AggregateCounter> {
    private AggregateCounterService countersService;

    @Autowired
    public AggregateCounterResource(AggregateCounterService countersService) {
        this.countersService = countersService;
    }
    @GetMapping("/list")
    public ResponseEntity<?>listAllCounters(){
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<AggregateCounter>getAllCounters = countersService.getAllAggregateCounters();
                httpStatus = OK;
                httpMessage = "Aggregate Counters Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, getAllCounters);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?>findById(@PathVariable("id") Long id){
        HttpStatus httpStatus;
        String httpMessage;
        try {
            AggregateCounter data = countersService.findById(id);
            httpStatus = OK;
            httpMessage = "Aggregate Counters Fetched Successfully";
            return responseWithData(httpStatus, httpMessage, data);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addAggregate(@RequestBody AggregateCounter aggregateCounter) throws Exception {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            AggregateCounter data = countersService.add(aggregateCounter);
            httpStatus = OK;
            httpMessage = "Aggregate Counters Add Successfully";
            return responseWithData(httpStatus, httpMessage, data);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?>updateAggregate(@RequestParam("currentId") Long aId, @RequestBody AggregateCounter aggregateCounter) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            aggregateCounter.setId(aId);
            countersService.update(aggregateCounter);
            httpStatus = OK;
            httpMessage = "Aggregate Counter Update Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e){
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAggregate(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            countersService.delete(id);
            httpStatus = OK;
            httpMessage = "Aggregate Counter Deleted Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchRule(@RequestBody() Map<String, Object> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<AggregateCounter> fetchedData = countersService.search(reqBody);
            httpStatus = OK;
            httpMessage = "Aggregate Counters Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
