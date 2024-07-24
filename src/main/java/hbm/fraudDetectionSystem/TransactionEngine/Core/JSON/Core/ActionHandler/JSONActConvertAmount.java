package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.ActionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils.JSONFieldHeaderDictionary;
import hbm.fraudDetectionSystem.TransactionEngine.Exception.TransactionEngineException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("jsonActConvertAmount")
@Slf4j
public class JSONActConvertAmount implements JSONActionProcessor {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Override
    public JsonNode run(JsonNode on, ObjectNode pn, String value, JSONFieldHeaderDictionary fConfig, String args) throws JsonProcessingException, TransactionEngineException {
        LOGGER.info(
                String.format(
                        "Start convert amount with value: %s",
                        value
                )
        );

        String v1 = value.replaceAll(fConfig.getFormatter().getFormat(), "");

        LOGGER.info(
                String.format(
                        "Finish convert, result: %s",
                        v1
                ), 1
        );

        return TextNode.valueOf(v1);
    }
}
