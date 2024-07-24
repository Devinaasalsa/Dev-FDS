package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.ActionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils.JSONFieldHeaderDictionary;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils.JSONHashHelper;
import hbm.fraudDetectionSystem.TransactionEngine.Exception.TransactionEngineException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("jsonActDecryptBase64")
@Slf4j
public class JSONActDecryptBase64 implements JSONActionProcessor {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Override
    public JsonNode run(JsonNode on, ObjectNode pn, String value, JSONFieldHeaderDictionary fConfig, String args) throws JsonProcessingException, TransactionEngineException {
        LOGGER.info(
                String.format(
                        "Start unhashing with base64 value: %s",
                        value
                )
        );

        String v1 = this.unhashBase64(value);

        LOGGER.info(
                String.format(
                        "Finish unhashing, result: %s",
                        v1
                ), 1
        );
        return TextNode.valueOf(v1);
    }

    protected String unhashBase64(String value) throws TransactionEngineException {
        try {
            return JSONHashHelper.unhashBase64(value);
        } catch (Exception e) {
            throw new TransactionEngineException("Unexpected error unhash base64", e);
        }
    }
}
