package hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint;

import hbm.fraudDetectionSystem.ChannelEngine.Domain.ChannelEndpointHelper;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans.CurrTrans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("channel_endpoint")
public class ChannelEndpointResource extends ResponseResourceEntity<Object> {
    protected final ChannelEndpointService channelEndpointService;

    @Autowired
    public ChannelEndpointResource(ChannelEndpointService channelEndpointService) {
        this.channelEndpointService = channelEndpointService;
    }

    @GetMapping("list/{id}")
    public ResponseEntity<?> fetchAll(@PathVariable("id") long configId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<ChannelEndpoint> fetchedData = channelEndpointService.fetchAll(configId);
            httpStatus = OK;
            httpMessage = "Channel Endpoint Fetched Successfully";
            return responseWithListObjectData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @GetMapping("find/{endpointId}")
    public ResponseEntity<?> findByEndpointId(@PathVariable("endpointId") long endpointId) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            ChannelEndpointHelper fetchedData = channelEndpointService.findByEndpointId(endpointId);
            httpStatus = OK;
            httpMessage = "Channel Endpoint Fetched Successfully";
            return responseWithData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithData(httpStatus, httpMessage, null);
        }
    }

    @PostMapping("add")
    public ResponseEntity<?> add(@RequestBody ChannelEndpointHelper reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            channelEndpointService.add(reqBody);
            httpStatus = OK;
            httpMessage = "Channel Endpoint Added Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("update")
    public ResponseEntity<?> update(@RequestBody ChannelEndpointHelper reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            channelEndpointService.update(reqBody);
            httpStatus = OK;
            httpMessage = "Channel Endpoint updated Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            channelEndpointService.delete(id);
            httpStatus = OK;
            httpMessage = "Channel Endpoint deleted Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }
}
