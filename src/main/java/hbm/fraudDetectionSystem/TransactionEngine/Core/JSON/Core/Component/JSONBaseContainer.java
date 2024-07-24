package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.ActionHandler.JSONActionHandler;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils.JSONFieldHeaderDictionary;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldAction.JSONFieldAction;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldConfiguration.JSONFieldConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldValue.JSONFieldValue;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONHeaderConfiguration.JSONHeaderConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.Exception.TransactionEngineException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Slf4j
public class JSONBaseContainer {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected List<JSONHeaderConfiguration> headerFieldConfigurations;
    protected List<JSONFieldConfiguration> fieldConfigurations;
    protected JSONActionHandler actionHandler;

    public JSONBaseContainer(List<JSONHeaderConfiguration> headerFieldConfigurations, List<JSONFieldConfiguration> fieldConfigurations) {
        this.headerFieldConfigurations = headerFieldConfigurations;
        this.fieldConfigurations = fieldConfigurations;
        this.actionHandler = new JSONActionHandler();
    }

    public void unpackHeader(JSONMsg m, JsonNode rawNode, ObjectNode prevNode) throws JsonProcessingException, TransactionEngineException {
        this.unpackHeaderNode(m, rawNode, prevNode);
    }

    public void unpack(JSONMsg m, JsonNode rawNode, ObjectNode prevNode) throws JsonProcessingException, TransactionEngineException {
        this.unpackRequestNode(m, rawNode, prevNode);
    }

    protected void unpackHeaderNode(JSONMsg m, JsonNode rawNode, ObjectNode prevNode) throws JsonProcessingException, TransactionEngineException {
        for (JSONHeaderConfiguration headerConfiguration : headerFieldConfigurations) {
            LOGGER.info(
                    String.format(
                            "Extract tag: [%s]",
                            headerConfiguration.getFieldName()
                    )
            );
            JsonNode fNode = rawNode.findPath(headerConfiguration.getFieldName());

            this.dataValueMapper(m, fNode, headerConfiguration, prevNode);
        }
    }

    protected void unpackRequestNode(JSONMsg m, JsonNode rawNode, ObjectNode prevNode) throws JsonProcessingException, TransactionEngineException {
        for (JSONFieldConfiguration fieldConfiguration : fieldConfigurations) {
            LOGGER.info(
                    String.format(
                            "Extract tag: [%s]",
                            fieldConfiguration.getFieldName()
                    )
            );
            JsonNode fNode = rawNode.findPath(fieldConfiguration.getFieldName());

            this.dataValueMapper(m, fNode, fieldConfiguration, prevNode);
        }
    }

    public void dataValueMapper(JSONMsg m, JsonNode fNode, JSONFieldHeaderDictionary fConfig, ObjectNode prevNode) throws JsonProcessingException, TransactionEngineException {
        switch (fConfig.getDataType().getTypeId().intValue()) {
            //String
            case 1:
                String stringValue = fNode.asText();
                this.validateValue(stringValue, fConfig.getLength(), fConfig.getValidValues());

                if (this.fConfigHaveAction(fConfig.getActions())) {
                    m.setField(fConfig.getFieldName(), stringValue);
                    LOGGER.info(
                            String.format(
                                    "Extracted value: [%s]",
                                    stringValue
                            )
                    );
                } else {
                    //TODO: Run action field
                    LOGGER.info("Tag have action, running all actions...");
                    JsonNode actionResult = this.actionHandler.run(m.fields, prevNode, stringValue, fConfig);
                    m.setField(fConfig.getFieldName(), actionResult);
                }
                break;

            //Decimal
            case 2:
                String decimalValue = this.internalDecimalFormatter(fNode.decimalValue());
                this.validateValue(decimalValue.replaceAll("\\.", ""), fConfig.getLength(), new HashSet<>());

                if (this.fConfigHaveAction(fConfig.getActions())) {
                    m.setField(fConfig.getFieldName(), decimalValue);
                    LOGGER.info(
                            String.format(
                                    "Extracted value: [%s]",
                                    decimalValue
                            )
                    );
                } else {
                    //TODO: Run action field
                }
                break;

            //Boolean
            case 3:
                boolean booleanValue = fNode.booleanValue();

                if (this.fConfigHaveAction(fConfig.getActions())) {
                    m.setField(fConfig.getFieldName(), booleanValue);
                } else {
                    //TODO: Run action field
                }
                break;

            //Object
            case 4:
                JSONBaseContainer oContainer = new JSONBaseContainer(null, fConfig.getChildField());

                JSONMsg om = new JSONMsg();
                om.setContainer(oContainer);
                om.unpack(fNode, prevNode);

                ObjectNode objectValue = om.fields;
                m.setField(fConfig.getFieldName(), objectValue);
                break;

            //Array
            case 5:
                this.validateArrayNode(fNode);
                ArrayNode aNode = new ObjectMapper().createArrayNode();

                for (JsonNode v1 : fNode) {
                    JSONBaseContainer aContainer = new JSONBaseContainer(null, fConfig.getChildField());

                    JSONMsg am = new JSONMsg();
                    am.setContainer(aContainer);
                    am.unpack(v1, prevNode);

                    ObjectNode arrayValue = am.fields;
                    aNode.add(arrayValue);
                }

                m.setField(fConfig.getFieldName(), aNode);
                break;

            //Integer
            case 6:
                int intValue = fNode.intValue();

                if (this.fConfigHaveAction(fConfig.getActions())) {
                    m.setField(fConfig.getFieldName(), intValue);
                    LOGGER.info(
                            String.format(
                                    "Extracted value: [%s]",
                                    intValue
                            )
                    );
                } else {
                    //TODO: Run action field
                }
                break;
        }
    }

    public void pack(ObjectNode rawNode, JsonNode prevNode) throws TransactionEngineException, JsonParseException {
        for (Iterator<String> it = rawNode.fieldNames(); it.hasNext(); ) {
            String fName = it.next();
            JSONFieldConfiguration fConfig = this.fieldConfigurations
                    .stream()
                    .filter(v1 -> v1.getFieldName().equals(fName))
                    .findFirst()
                    .orElseThrow(() -> new TransactionEngineException("Field Config not found"));

            LOGGER.info(
                    String.format(
                            "Prepare tag: [%s]",
                            fConfig.getFieldName()
                    )
            );

            String value = rawNode.findPath(fName).asText();
            this.validateValue(value, fConfig.getLength(), fConfig.getValidValues());

            switch (fConfig.getDataType().getTypeId().intValue()) {
                case 1:
                    if (!this.fConfigHaveAction(fConfig.getActions())) {
                        LOGGER.info("Tag have action, running all actions...");

                        JsonNode actionResult = this.actionHandler.run(rawNode, (ObjectNode) prevNode, value, fConfig);
                        rawNode.set(fName, actionResult);

                        LOGGER.info(
                                String.format(
                                        "Prepared value: [%s]",
                                        actionResult.asText()
                                )
                        );
                    } else {
                        LOGGER.info(
                                String.format(
                                        "Prepared value: [%s]",
                                        value
                                ),
                                1
                        );
                    }
            }
        }
    }


    protected void validateValue(String value, int length, Set<JSONFieldValue> validValues) throws TransactionEngineException {
        this.validateLength(value, length);
        this.validateValueWithValidValue(value, validValues);
    }

    protected void validateLength(String value, int length) throws TransactionEngineException {
        if (length > 0) {
            if (value.length() > length) {
                throw new TransactionEngineException(
                        String.format(
                                "[%s] length is too long, max [%s]",
                                value, length
                        )
                );
            }
        }
    }

    protected void validateValueWithValidValue(String value, Set<JSONFieldValue> validValues) throws TransactionEngineException {
        if (!validValues.isEmpty() && validValues.stream().noneMatch(v1 -> v1.getValue().equals(value))) {
            throw new TransactionEngineException(
                    String.format(
                            "value: %s not valid",
                            value
                    )
            );
        }
    }

    protected void validateArrayNode(JsonNode fNode) throws TransactionEngineException {
        if (!fNode.isArray()) {
            throw new TransactionEngineException("Field isn't array");
        }
    }

    protected boolean fConfigHaveAction(Set<JSONFieldAction> actions) {
        return actions.isEmpty();
    }

    protected String internalDecimalFormatter(BigDecimal value) {
        return new DecimalFormat("0.00").format(value);
    }
}

