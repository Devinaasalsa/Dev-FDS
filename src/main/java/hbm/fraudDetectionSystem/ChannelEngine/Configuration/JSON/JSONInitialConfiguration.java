package hbm.fraudDetectionSystem.ChannelEngine.Configuration.JSON;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hbm.fraudDetectionSystem.ChannelEngine.Core.JSON.JSONServer;
import hbm.fraudDetectionSystem.ChannelEngine.Exception.JSONChannelException;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration.ChannelConfigurationService;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.HttpResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"v2", "Api/v1/Product"})
public class JSONInitialConfiguration {
    @Value("${json.baseEndpoint.path}")
    public String[] jsonBaseEndpoint;
    protected final JSONServer jsonServer;
    protected final ChannelConfigurationService channelConfigurationService;

    @Autowired
    public JSONInitialConfiguration(JSONServer jsonServer, ChannelConfigurationService channelConfigurationService) {
        this.jsonServer = jsonServer;
        this.channelConfigurationService = channelConfigurationService;
    }

    @PostMapping("/**")
    public Object JSONInitialConfig(
            HttpServletRequest request
    ) throws IOException {
        long begin = System.currentTimeMillis();

        try {
            UUID uniqueID = UUID.randomUUID();
            String uID = uniqueID.toString();
            MDC.put("req_id", uID);

            String baseEndpoint = this.extractBaseUrl(request.getRequestURI());
            String url = request.getRequestURI().replace("/" + baseEndpoint + "/", "");

            return this.jsonServer.run(
                    request,
                    baseEndpoint,
                    url,
                    this.getRequestHeader(request),
                    this.getRequestValue(request)
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MDC.remove("req_id");
            long end = System.currentTimeMillis();
            long time = end - begin;

            System.out.println("Elapsed Time: " + time + " milli seconds");
        }
    }

    @ControllerAdvice
    public static class ExceptionControllerAdvice {
        @ExceptionHandler(JSONChannelException.class)
        public ResponseEntity<?> handleNotFoundException() {
            HttpStatus status = HttpStatus.NOT_FOUND;
            return new ResponseEntity<>(new HttpResponse<>(status.value(), status, status.getReasonPhrase().toUpperCase(),
                    null), status);
        }
    }

    protected String extractBaseUrl(String fullURI) {
        String[] baseUrls = this.jsonBaseEndpoint;
        for (String baseUrl : baseUrls) {
            if (fullURI.startsWith(baseUrl)) {
                return baseUrl.startsWith("/") ? baseUrl.substring(1) : baseUrl;
            }
        }
        return "";
    }

    protected JsonNode getRequestHeader(HttpServletRequest request) {
        Map<String, String> headersMap = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headersMap.put(headerName, headerValue);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.valueToTree(headersMap);
    }

    protected JsonNode getRequestValue(HttpServletRequest request) throws IOException {
        String contentType = request.getContentType();

        if (this.isFormUrlEncoded(contentType)) {
            return this.getFormUrlEncodedValue(request);
        } else if (this.isJsonValue(contentType)) {
            return this.getJsonValue(request);
        } else {
            throw new RuntimeException("Content Type not Supported");
        }
    }

    protected boolean isFormUrlEncoded(String contentType) {
        return contentType != null && contentType.equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    protected boolean isJsonValue(String contentType) {
        return contentType != null && contentType.equals(MediaType.APPLICATION_JSON_VALUE);
    }

    protected JsonNode getFormUrlEncodedValue(HttpServletRequest request) {
        ObjectMapper om = new ObjectMapper();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> dataMap = new HashMap<>();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values.length > 0) {
                dataMap.put(key, values[0]);
            }
        }

        return om.valueToTree(dataMap);
    }

    protected JsonNode getJsonValue(HttpServletRequest request) throws IOException {
        ObjectMapper om = new ObjectMapper();
        String jsonData = request.getReader().lines().collect(Collectors.joining());
        return om.readTree(jsonData);
    }
}
