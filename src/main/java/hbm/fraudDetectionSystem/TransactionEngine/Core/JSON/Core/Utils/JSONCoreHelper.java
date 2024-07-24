package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration.ChannelConfiguration;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrAddtTrans.CurrAddtTrans;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtRespCode.ExtRespCode;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtTransType.ExtTransType;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransTypeTab.TransTypeTab;
import hbm.fraudDetectionSystem.GeneralComponent.Utility.DateHelper;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Component.JSONBaseContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Component.JSONMsg;
import hbm.fraudDetectionSystem.TransactionEngine.Exception.TransactionEngineException;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransDataAttribute.TransDataAttribute;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransMsgCfg.TransMsgCfg;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class JSONCoreHelper {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected void unpackHeader(JSONBaseContainer reqContainer, JsonNode rawNode) throws JsonProcessingException, TransactionEngineException {
        JSONMsg reqMsg = new JSONMsg();
        reqMsg.setContainer(reqContainer);
        reqMsg.unpackHeader(rawNode, null);
    }

    protected JSONMsg unpackMessage(JSONBaseContainer reqContainer, JsonNode rawNode) throws JsonProcessingException, TransactionEngineException {
        JSONMsg reqMsg = new JSONMsg();
        reqMsg.setContainer(reqContainer);
        reqMsg.unpack(rawNode, null);
        return reqMsg;
    }

    protected void packResponse(JSONMsg m, JSONMsg pm, String configId, String transType, Map<String, String> preparedData, Map<String, Map<String, List<TransMsgCfg>>> transMsgConfigs) throws TransactionEngineException, JsonParseException {
        List<TransMsgCfg> msgCfgs = this.getMsgCfg(configId, transType, transMsgConfigs);
        for (TransMsgCfg v1 : msgCfgs) {
            switch (v1.getDer().getNotation()) {
                case "m":
                case "c":
                case "o":
                    String value = preparedData.get(v1.getTransAttr().getAttribute());
                    m.setField(v1.getFld(), value);
                    break;

                default:
                    break;
            }
        }

        m.pack(pm.getFields());
    }

    protected JSONMsg createRespMsg(JSONBaseContainer container) {
        JSONMsg m = new JSONMsg();
        m.setContainer(container);
        return m;
    }

    protected String getConfigId(ChannelConfiguration cc) {
        return cc.getMsgConfig().getConfigId().toString();
    }

    protected String getPID(ChannelConfiguration cc) {
        return cc.getPid().toString();
    }

    protected TransTypeTab retrieveTransTypeFromMapping(String endpoint, long configId, Map<Long, List<ExtTransType>> extTransType) throws TransactionEngineException {
        LOGGER.info(
                String.format(
                        "Checking endpoint: %s with configId: %s",
                        endpoint, configId
                )
        );

        List<ExtTransType> filteredTransType = extTransType.get(configId);
        if (filteredTransType != null) {
            filteredTransType = filteredTransType
                    .stream()
                    .filter(data -> Objects.equals(data.getTransType(), endpoint))
                    .collect(Collectors.toList());
            if (filteredTransType.isEmpty()) {
                throw new TransactionEngineException("This endpoint not have any transaction type configuration, please check configuration!!!");
            }
        } else {
            throw new TransactionEngineException("This config id not have any transaction type configuration, please check configuration!!!");
        }

        TransTypeTab v1 = filteredTransType.get(0).getIntTransType();

        LOGGER.info(
                String.format(
                        "Transaction Type: %s | Description: %s",
                        v1.getCode(),
                        v1.getDescription()
                )
        );

        return v1;
    }

    protected ExtRespCode retrieveRespCodeFromMapping(String responseCode, long configId, Map<Long, List<ExtRespCode>> extRespCodes) throws TransactionEngineException {
        LOGGER.info(
                String.format(
                        "Checking responseCode: %s with configId: %s",
                        responseCode, configId
                )
        );

        List<ExtRespCode> filteredRespCode = extRespCodes.get(configId);
        if (filteredRespCode != null) {
            filteredRespCode = filteredRespCode
                    .stream()
                    .filter(data -> Objects.equals(data.getIntResp().getCode(), responseCode))
                    .collect(Collectors.toList());
            if (filteredRespCode.isEmpty()) {
                throw new TransactionEngineException("This endpoint not have any response code configuration, please check configuration!!!");
            }
        } else {
            throw new TransactionEngineException("This config id not have any response code configuration, please check configuration!!!");
        }

        ExtRespCode v1 = filteredRespCode.get(0);

        LOGGER.info(
                String.format(
                        "Response Code: %s | External Code: %s | Description: %s",
                        v1.getIntResp().getCode(),
                        v1.getRespCode(),
                        v1.getIntResp().getDescription()
                )
        );

        return v1;
    }

    protected void setPreparedData(JSONMsg m, Map<String, String> preparedData, List<CurrAddtTrans> preparedAddtData, List<TransDataAttribute> dataAttributes) {
        for (TransDataAttribute attr : dataAttributes) {
            if (this.notSystemAttr(attr) && preparedData.get(attr.getAttribute()) == null) {
                String fieldTag = retrieveAttrFieldTag(attr);
                String value = m.getValue(fieldTag);
                preparedData.put(attr.getAttribute(), value == null ? null : value.trim());

                if (attr.getAddtData() && value != null) {
                    CurrAddtTrans currAddtTrans = CurrAddtTrans.builder()
                            .attr(attr.getAttribute())
                            .description(attr.getDescription())
                            .value(value.trim()).build();
                    preparedAddtData.add(currAddtTrans);
                }
            } else if (preparedData.get(attr.getAttribute()) != null && attr.getAddtData()) {
                CurrAddtTrans currAddtTrans = CurrAddtTrans.builder()
                        .attr(attr.getAttribute())
                        .description(attr.getDescription())
                        .value(
                                preparedData.get(attr.getAttribute())
                        ).build();
                preparedAddtData.add(currAddtTrans);
            }
        }
    }

    protected void setSysdateData(String sysdate, List<CurrAddtTrans> preparedAddtData) {
        CurrAddtTrans sysday = CurrAddtTrans.builder()
                .attr("sysday")
                .description("Sysday")
                .value(DateHelper.getDay(sysdate)).build();
        preparedAddtData.add(sysday);

        CurrAddtTrans syshour = CurrAddtTrans.builder()
                .attr("syshour")
                .description("Syshour")
                .value(DateHelper.getHour(sysdate)).build();
        preparedAddtData.add(syshour);

        CurrAddtTrans syssec = CurrAddtTrans.builder()
                .attr("syssec")
                .description("Syssec")
                .value(DateHelper.getSec(sysdate)).build();
        preparedAddtData.add(syssec);
    }

    protected void setPreparedData(JSONMsg m, Map<String, String> preparedData, List<TransDataAttribute> dataAttributes) {
        for (TransDataAttribute attr : dataAttributes) {
            if (this.notSystemAttr(attr) && preparedData.get(attr.getAttribute()) == null) {
                String fieldTag = retrieveAttrFieldTag(attr);
                String value = m.getValue(fieldTag);
                preparedData.put(attr.getAttribute(), value == null ? "" : value);
            }
        }
    }

    protected void reviewPreparedData(Map<String, String> preparedData) {
        for (Map.Entry<String, String> entry : preparedData.entrySet()) {
            String attr = entry.getKey();
            String value = entry.getValue();

            LOGGER.info(
                    String.format(
                            "Found: [Attr: %s | Value: %s]",
                            attr, value
                    )
            );
        }
    }

    protected String retrieveAttrFieldTag(TransDataAttribute dataAttribute) {
        return dataAttribute.getFieldTag() != null ? dataAttribute.getFieldTag().trim() : null;
    }

    protected boolean notSystemAttr(TransDataAttribute attr) {
        return attr.getConfigId() != null && attr.getEndpoint() != null;
    }

    protected String validateResponseCode(String temp) {
        return temp != null ?
                temp : "-1";
    }

    protected List<TransMsgCfg> getMsgCfg(String configId, String transType, Map<String, Map<String, List<TransMsgCfg>>> transMsgConfigs) throws TransactionEngineException {
        Map<String, List<TransMsgCfg>> v1 = this.filterMsgCfgByConfigId(configId, transMsgConfigs);
        return this.filterMsgCfgByTransType(transType, v1);
    }

    protected Map<String, List<TransMsgCfg>> filterMsgCfgByConfigId(String configId, Map<String, Map<String, List<TransMsgCfg>>> transMsgConfigs) throws TransactionEngineException {
        Map<String, List<TransMsgCfg>> v1 = transMsgConfigs.get(configId);
        if (v1 == null) {
            throw new TransactionEngineException("Trans Msg Cfg by config id not found");
        }
        return v1;
    }

    protected List<TransMsgCfg> filterMsgCfgByTransType(String transType, Map<String, List<TransMsgCfg>> v1) throws TransactionEngineException {
        List<TransMsgCfg> v2 = v1.get(transType);
        if (v2 == null) {
            throw new TransactionEngineException("Trans Msg Cfg by trans type not found");
        }
        return v2;
    }
}
