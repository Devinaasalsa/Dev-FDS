package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.RespTab;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule.Rule;
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
@RequestMapping("resp_tab")
public class RespTabResource extends ResponseResourceEntity<RespTab> {
    protected final RespTabService respTabService;

    @Autowired
    public RespTabResource(RespTabService respTabService) {
        this.respTabService = respTabService;
    }

    @GetMapping("list")
    public ResponseEntity<?> list() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<RespTab> respTabs = respTabService.findAll();
            httpStatus = OK;
            httpMessage = "Resp Code Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, respTabs);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addRules(@RequestBody RespTab data) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            respTabService.add(data);
            httpStatus = OK;
            httpMessage = "Code Add Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateRules(@RequestBody RespTab data) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            respTabService.update(data);
            httpStatus = OK;
            httpMessage = "Code Update Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            respTabService.delete(id);
            httpStatus = OK;
            httpMessage = "Code Deleted Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody() Map<String, Object> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<RespTab> fetchedData = respTabService.search(reqBody);
            httpStatus = OK;
            httpMessage = "Resp Code Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
