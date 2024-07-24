package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration.ChannelConfiguration;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtTransType.ExtTransType;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransTypeTab.TransTypeTab;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Component.JSONBaseContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Component.JSONMsg;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils.JSONCoreHelper;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransDataAttribute.TransDataAttribute;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransMsgCfg.TransMsgCfg;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class JSONAuthListener extends JSONCoreHelper {
    /*
        String = ConfigId
        String = TransType
     */
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final Map<String, Map<String, List<TransMsgCfg>>> transMsgConfigs;
    protected final Map<Long, List<ExtTransType>> extTransType;

        @Autowired
    public JSONAuthListener(Map<String, Map<String, List<TransMsgCfg>>> transMsgConfigs, Map<Long, List<ExtTransType>> extTransType) {
        this.transMsgConfigs = transMsgConfigs;
        this.extTransType = extTransType;
    }

    public ObjectNode process(ChannelConfiguration cc, JsonNode rawHeaderNode, JsonNode rawNode, JSONBaseContainer reqContainer, JSONBaseContainer respContainer, List<TransDataAttribute> dataAttributes) throws JsonProcessingException {
        LOGGER.info("Receive data from [Channel Engine]");

        ObjectMapper om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            LOGGER.info("Extract request body");
            JSONMsg reqMsg = this.unpackMessage(reqContainer, rawNode);
            JSONMsg respMsg = this.createRespMsg(respContainer);
            String configId = getConfigId(cc);
            Map<String, String> preparedData = new HashMap<>();

            //Mapping Trans Type
            TransTypeTab transType = this.retrieveTransTypeFromMapping(cc.getEndpoint().getUrl(), cc.getMsgConfig().getConfigId(), this.extTransType);
            preparedData.put("transType", transType.getCode());
            preparedData.put("transTypeDesc", transType.getDescription());

            LOGGER.info("Start mapping value to trans attribute...");
            setPreparedData(reqMsg, preparedData, dataAttributes);

            LOGGER.info("Prepare response body");
            this.packResponse(respMsg, reqMsg, configId, transType.getCode(), preparedData, this.transMsgConfigs);
            LOGGER.info(
                    String.format(
                            "Prepared response body: \n%s",
                            om.writeValueAsString(respMsg.getFields())
                    )
            );

            LOGGER.info("Send data to [Channel Engine]");

            return respMsg.getFields();
        } catch (Exception e) {
            LOGGER.info(String.format("Error: %s", e.getMessage()));
            LOGGER.error("", e);

            LOGGER.info("Prepare response body");

            ObjectNode on = om.createObjectNode();
            on.put("rCode", cc.getErrorCode());
            on.put("message", e.getMessage());

            LOGGER.info(
                    String.format(
                            "Prepared response body: \n%s",
                            om.writeValueAsString(on)
                    )
            );

            LOGGER.info("Send data to [Channel Engine]");
            return on;
        }
    }
}
