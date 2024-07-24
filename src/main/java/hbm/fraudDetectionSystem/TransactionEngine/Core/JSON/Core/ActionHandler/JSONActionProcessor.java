package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.ActionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Component.JSONMsg;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils.JSONFieldHeaderDictionary;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldConfiguration.JSONFieldConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.Exception.TransactionEngineException;

public interface JSONActionProcessor {
    JsonNode run(JsonNode on, ObjectNode pn, String value, JSONFieldHeaderDictionary fConfig, String args) throws JsonProcessingException, TransactionEngineException;
}
