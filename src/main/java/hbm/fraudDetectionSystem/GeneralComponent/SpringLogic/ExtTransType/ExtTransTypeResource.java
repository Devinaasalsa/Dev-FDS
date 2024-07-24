package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtTransType;

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
@RequestMapping("ext_trans_type")
public class ExtTransTypeResource extends ResponseResourceEntity<ExtTransType> {
    protected final ExtTransTypeService extTransTypeService;

    @Autowired
    public ExtTransTypeResource(ExtTransTypeService extTransTypeService) {
        this.extTransTypeService = extTransTypeService;
    }

    @GetMapping("list/{configId}")
    public ResponseEntity<?> list(@PathVariable("configId") long configId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<ExtTransType> extTransTypes = extTransTypeService.fetchAllByConfigId(configId);
            httpStatus = OK;
            httpMessage = "Ext Trans Type Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, extTransTypes);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("add")
    public ResponseEntity<?> add(@RequestBody ExtTransType extTransType) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            extTransTypeService.add(extTransType);
            httpStatus = OK;
            httpMessage = "Ext Trans Type Add Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("update")
    public ResponseEntity<?> update(@RequestBody ExtTransType extTransType) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            extTransTypeService.update(extTransType);
            httpStatus = OK;
            httpMessage = "Ext Trans Type Update Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            extTransTypeService.delete(id);
            httpStatus = OK;
            httpMessage = "Ext Trans Type Delete Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("search")
    public ResponseEntity<?> list(@RequestBody Map<String, Object> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<ExtTransType> extRespCode = extTransTypeService.search(reqBody);
            httpStatus = OK;
            httpMessage = "Ext Trans Type Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, extRespCode);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
