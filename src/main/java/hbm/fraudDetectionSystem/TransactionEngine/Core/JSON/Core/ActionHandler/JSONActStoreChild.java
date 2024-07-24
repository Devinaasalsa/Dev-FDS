package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.ActionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Component.JSONBaseContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Component.JSONMsg;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils.JSONFieldHeaderDictionary;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldConfiguration.JSONFieldConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.Exception.TransactionEngineException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component("jsonActStoreChild")
@Slf4j
public class JSONActStoreChild implements JSONActionProcessor {
    @Override
    public JsonNode run(JsonNode on, ObjectNode pn, String value, JSONFieldHeaderDictionary fConfig, String args) throws JsonProcessingException, TransactionEngineException {
        try {
            JSONBaseContainer container = new JSONBaseContainer(
                    new ArrayList<>(), fConfig.getChildField()
            );

            JSONMsg m = new JSONMsg();
            m.setContainer(container);
            m.unpack(on, pn);

            return m.getFields();
        } catch (Exception e) {
            throw new TransactionEngineException("Unexpected error Store Child", e);
        }
    }
}
