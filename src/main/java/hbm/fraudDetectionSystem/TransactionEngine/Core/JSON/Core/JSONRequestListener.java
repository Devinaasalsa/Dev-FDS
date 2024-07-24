package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration.ChannelConfiguration;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.AllSequences.AllSequencesService;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrAddtTrans.CurrAddtTrans;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans.CurrTrans;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans.CurrTransServiceImpl;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtRespCode.ExtRespCode;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtTransType.ExtTransType;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransTypeTab.TransTypeTab;
import hbm.fraudDetectionSystem.GeneralComponent.Utility.DateHelper;
import hbm.fraudDetectionSystem.GeneralComponent.Utility.SequenceUpdaterService;
import hbm.fraudDetectionSystem.TransactionEngine.Core.Fraud.FraudDetection;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class JSONRequestListener extends JSONCoreHelper {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final AllSequencesService allSequencesService;
    protected final CurrTransServiceImpl currTransService;
    protected final FraudDetection fraudDetection;
    protected final Map<Long, List<ExtTransType>> extTransType;
    protected final Map<Long, List<ExtRespCode>> extRespCodes;
    /*
        String = ConfigId
        String = TransType
     */
    protected final Map<String, Map<String, List<TransMsgCfg>>> transMsgConfigs;

    @Autowired
    public JSONRequestListener(AllSequencesService allSequencesService, CurrTransServiceImpl currTransService, FraudDetection fraudDetection, Map<Long, List<ExtTransType>> extTransType, Map<Long, List<ExtRespCode>> extRespCodes, Map<String, Map<String, List<TransMsgCfg>>> transMsgConfigs) {
        this.allSequencesService = allSequencesService;
        this.currTransService = currTransService;
        this.fraudDetection = fraudDetection;
        this.extTransType = extTransType;
        this.extRespCodes = extRespCodes;
        this.transMsgConfigs = transMsgConfigs;
    }

    public ObjectNode process(ChannelConfiguration cc, JsonNode rawHeaderNode, JsonNode rawNode, JSONBaseContainer reqContainer, JSONBaseContainer respContainer, List<TransDataAttribute> dataAttributes) throws JsonProcessingException {
        LOGGER.info("Receive data from [Channel Engine]");

        ObjectMapper om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            String configId = getConfigId(cc);
            Map<String, String> preparedData = new HashMap<>();
            List<CurrAddtTrans> preparedAddtData = new ArrayList<>();

            if (cc.getMsgConfig().isHasHeader()) {
                LOGGER.info("Extract request header");
                this.unpackHeader(reqContainer, rawHeaderNode);
            }

            LOGGER.info("Extract request body");
            JSONMsg reqMsg = this.unpackMessage(reqContainer, rawNode);
            JSONMsg respMsg = this.createRespMsg(respContainer);

            long utnrno = allSequencesService.updateSeqNumber(SequenceUpdaterService.nextSeq(), 1);

            preparedData.put("utrnno", Long.toString(utnrno));

            String sysdate = DateHelper.convertTimestamp(new Timestamp(System.currentTimeMillis()).toString());

            //Generate sysDate
            preparedData.put("sysdate", sysdate);
            preparedData.put("sysday", DateHelper.getDay(sysdate));
            preparedData.put("syshour", DateHelper.getHour(sysdate));
            preparedData.put("syssec", DateHelper.getSec(sysdate));

            //Set PID
            preparedData.put("pid", getPID(cc));

            //Set ConfigId
            preparedData.put("configId", configId);

            //Mapping Trans Type
            TransTypeTab transType = this.retrieveTransTypeFromMapping(cc.getEndpoint().getUrl(), cc.getMsgConfig().getConfigId(), this.extTransType);
            preparedData.put("transType", transType.getCode());
            preparedData.put("transTypeDesc", transType.getDescription());

            //Set MTI
            String mti = cc.getEndpoint().getSysMti();
            if (mti != null) {
                preparedData.put("mti", mti);
            }

            //Prepare static data
            LOGGER.info("Start mapping value to trans attribute...");
            setPreparedData(reqMsg, preparedData, preparedAddtData, dataAttributes);
            this.reviewPreparedData(preparedData);

            LOGGER.info("Send data to [Fraud Detection]");


            String intRespCode = this.validateResponseCode(
                    this.fraudDetection.detectTransaction(preparedData)
            );
            LOGGER.info("Receive data from [Fraud Detection]");

            ExtRespCode respCode = this.retrieveRespCodeFromMapping(intRespCode, cc.getMsgConfig().getConfigId(), this.extRespCodes);
            preparedData.put("extRespCode", respCode.getRespCode());
            preparedData.put("respCode", respCode.getIntResp().getCode());
            preparedData.put("respCodeDesc", respCode.getIntResp().getDescription());

            CurrTrans currTrans = this.currTransService.saveTransaction(preparedData, preparedAddtData, false);
            LOGGER.info(
                    String.format(
                            "Showing all variable value: \n< ALL SET VARIABLES OF CURR TRANS > \n%s",
                            currTrans.toString()
                    )
            );

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
