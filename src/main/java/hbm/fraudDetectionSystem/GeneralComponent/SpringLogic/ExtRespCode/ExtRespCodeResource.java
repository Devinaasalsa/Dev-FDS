package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtRespCode;

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
@RequestMapping("ext_resp_code")
public class ExtRespCodeResource extends ResponseResourceEntity<ExtRespCode> {
    protected final ExtRespCodeService extRespCodeService;

    @Autowired
    public ExtRespCodeResource(ExtRespCodeService extRespCodeService) {
        this.extRespCodeService = extRespCodeService;
    }

    @GetMapping("list/{configId}")
    public ResponseEntity<?> list(@PathVariable("configId") long configId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<ExtRespCode> extRespCode = extRespCodeService.fetchAllByConfigId(configId);
            httpStatus = OK;
            httpMessage = "Ext Resp Code Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, extRespCode);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("add")
    public ResponseEntity<?> add(@RequestBody ExtRespCode extRespCode) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            extRespCodeService.add(extRespCode);
            httpStatus = OK;
            httpMessage = "Ext Resp Code Add Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("update")
    public ResponseEntity<?> update(@RequestBody ExtRespCode extRespCode) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            extRespCodeService.update(extRespCode);
            httpStatus = OK;
            httpMessage = "Ext Resp Code Update Successfully";
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
            extRespCodeService.delete(id);
            httpStatus = OK;
            httpMessage = "Ext Resp Code Delete Successfully";
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
            List<ExtRespCode> extRespCode = extRespCodeService.search(reqBody);
            httpStatus = OK;
            httpMessage = "Ext Resp Code Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, extRespCode);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }
}
