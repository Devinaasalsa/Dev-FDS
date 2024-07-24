package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransTypeTab;

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
@RequestMapping("trans_type_tab")
public class TransTypeTabResource extends ResponseResourceEntity<TransTypeTab> {
    protected final TransTypeTabService transTypeTabService;

    @Autowired
    public TransTypeTabResource(TransTypeTabService transTypeTabService) {
        this.transTypeTabService = transTypeTabService;
    }

    @GetMapping("list")
    public ResponseEntity<?> list() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<TransTypeTab> transTypeTabs = transTypeTabService.findAll();
            httpStatus = OK;
            httpMessage = "Trans Type Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, transTypeTabs);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addRules(@RequestBody TransTypeTab data) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            transTypeTabService.add(data);
            httpStatus = OK;
            httpMessage = "Trans type Add Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateRules(@RequestBody TransTypeTab data) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            transTypeTabService.update(data);
            httpStatus = OK;
            httpMessage = "Trans type Update Successfully";
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
            transTypeTabService.delete(id);
            httpStatus = OK;
            httpMessage = "Trans type Deleted Successfully";
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
            List<TransTypeTab> fetchedData = transTypeTabService.search(reqBody);
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
