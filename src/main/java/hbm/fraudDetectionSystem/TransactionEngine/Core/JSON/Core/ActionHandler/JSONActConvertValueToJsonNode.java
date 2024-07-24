package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.ActionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils.JSONFieldHeaderDictionary;
import hbm.fraudDetectionSystem.TransactionEngine.Exception.TransactionEngineException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("jsonActConvertValueToJsonNode")
@Slf4j
public class JSONActConvertValueToJsonNode implements JSONActionProcessor {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public JsonNode run(JsonNode on, ObjectNode pn, String value, JSONFieldHeaderDictionary fConfig, String args) throws JsonProcessingException, TransactionEngineException {
        try {
            LOGGER.info(
                    String.format(
                            "Raw value: %s",
                            value
                    )
            );

            ObjectMapper om = new ObjectMapper();
            om.enable(SerializationFeature.INDENT_OUTPUT);

            LOGGER.info(
                    String.format(
                            "Pattern: %s",
                            args
                    )
            );

            JsonNode rn;
            if (args != null) {
                rn = this.convertToJSON(value, args);
            } else {
                rn = this.convertStringJSONToJSON(value);
            }

            LOGGER.info(
                    String.format(
                            "result converted value: %s",
                            om.writeValueAsString(rn)
                    )
            );
            return rn;
        } catch (Exception e) {
            throw new TransactionEngineException("Unexpected error convert to JSON node", e);
        }
    }

    protected JsonNode convertToJSON(String rawValue, String pattern) {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonNode = objectMapper.createObjectNode();

        Pattern keyValuePattern = Pattern.compile(pattern);
        Matcher matcher = keyValuePattern.matcher(rawValue);

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            jsonNode.put(key, value);
        }

        return jsonNode;
    }

    protected JsonNode convertStringJSONToJSON(String rawValue) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(rawValue);
    }
}