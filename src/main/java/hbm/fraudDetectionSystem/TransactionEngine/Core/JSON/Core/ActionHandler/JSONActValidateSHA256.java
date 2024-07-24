package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.ActionHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils.JSONFieldHeaderDictionary;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils.JSONHashHelper;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldConfiguration.JSONFieldConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldFormatter.JSONFieldFormatter;
import hbm.fraudDetectionSystem.TransactionEngine.Exception.TransactionEngineException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("jsonActValidateSHA256")
@Slf4j
public class JSONActValidateSHA256 implements JSONActionProcessor {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public JsonNode run(JsonNode on, ObjectNode pn, String value, JSONFieldHeaderDictionary fConfig, String args) throws TransactionEngineException {
        try {
            LOGGER.info(
                    String.format(
                            "Initial value: %s",
                            value
                    )
            );

            LOGGER.info("Build compare value with expression");

            JSONFieldFormatter formatter = fConfig.getFormatter();

            LOGGER.info(
                    String.format(
                            "Expression: %s",
                            formatter.getFormat()
                    )
            );

            this.validateFormatExpression(formatter);
            String formattedValue = this.evaluateFormatExpression(on, formatter.getFormat());

            LOGGER.info(
                    String.format(
                            "Result: %s",
                            formattedValue
                    )
            );

            String hashValue = JSONHashHelper.hashSHA256(formattedValue);

            LOGGER.info(
                    String.format(
                            "Hash result: %s",
                            hashValue
                    )
            );

            LOGGER.info(
                    "Validate initial value with compare value"
            );

            this.validateHashValue(value, hashValue);

            LOGGER.info(
                    "Status: [MATCH]"
            );

            return TextNode.valueOf(value);
        } catch (TransactionEngineException e) {
            throw e;
        } catch (Exception e) {
            throw new TransactionEngineException("Unexpected error validate SHA256", e);
        }
    }

    protected void validateFormatExpression(JSONFieldFormatter formatter) throws TransactionEngineException {
        if (formatter == null) {
            throw new TransactionEngineException("Formatter is null");
        }
    }

    protected String evaluateFormatExpression(JsonNode dataModel, String formatExpression) {
        StringBuilder formattedValue = new StringBuilder();
        for (String format : formatExpression.split("\\+")) {
            JsonNode v1 = dataModel.findPath(format.trim());
            if (!v1.isMissingNode()) {
                formattedValue.append(v1.asText());
            } else
                formattedValue.append(format.trim());
        }

        return formattedValue.toString();
    }

    protected void validateHashValue(String value, String hashValue) throws TransactionEngineException {
        if (!value.equalsIgnoreCase(hashValue)) {
            throw new TransactionEngineException("SHA256 is not match");
        }
    }
}
