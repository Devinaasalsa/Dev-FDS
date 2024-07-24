package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Phase2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration.ChannelConfiguration;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint.ChannelEndpoint;
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
public class JSONResponseListener extends JSONCoreHelper {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final AllSequencesService allSequencesService;
    protected final CurrTransServiceImpl currTransService;
    protected final FraudDetection fraudDetection;
    /*
        String = ConfigId
        String = TransType
     */
    protected final Map<String, Map<String, List<TransMsgCfg>>> transMsgConfigs;
    protected final Map<Long, List<ExtTransType>> extTransType;
    protected final Map<Long, List<ExtRespCode>> extRespCodes;

    @Autowired
    public JSONResponseListener(AllSequencesService allSequencesService, CurrTransServiceImpl currTransService, FraudDetection fraudDetection, Map<String, Map<String, List<TransMsgCfg>>> transMsgConfigs, Map<Long, List<ExtTransType>> extTransType, Map<Long, List<ExtRespCode>> extRespCodes) {
        this.allSequencesService = allSequencesService;
        this.currTransService = currTransService;
        this.fraudDetection = fraudDetection;
        this.transMsgConfigs = transMsgConfigs;
        this.extTransType = extTransType;
        this.extRespCodes = extRespCodes;
    }

    public void process(ChannelConfiguration cc, ArrayNode rawData, JSONBaseContainer respContainer, List<TransDataAttribute> dataAttributes, ChannelEndpoint endpoint) {
        LOGGER.info("Receive data from [Channel Engine]");

        ObjectMapper om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            String configId = getConfigId(cc);
            LOGGER.info("Extract response body");

            for (JsonNode respData : rawData) {
                Map<String, String> preparedData = new HashMap<>();
                List<CurrAddtTrans> preparedAddtData = new ArrayList<>();

                JSONMsg respMsg = this.unpackMessage(respContainer, respData);

                long utnrno = allSequencesService.updateSeqNumber(SequenceUpdaterService.nextSeq(), 1);

                preparedData.put("utrnno", Long.toString(utnrno));

                //Generate sysDate
//                preparedData.put("sysdate", DateHelper.convertTimestamp(new Timestamp(System.currentTimeMillis()).toString()));

                //Set PID
                preparedData.put("pid", getPID(cc));

                //Set ConfigId
                preparedData.put("configId", configId);

                //Mapping Trans Type
                TransTypeTab transType = this.retrieveTransTypeFromMapping(endpoint.getUrl(), cc.getMsgConfig().getConfigId(), this.extTransType);
                preparedData.put("transType", transType.getCode());
                preparedData.put("transTypeDesc", transType.getDescription());

                //Set MTI
                String mti = endpoint.getSysMti();
                if (mti != null) {
                    preparedData.put("mti", mti);
                }

                //Prepare static data
                LOGGER.info("Start mapping value to trans attribute...");
                setPreparedData(respMsg, preparedData, preparedAddtData, dataAttributes);
                setSysdateData(preparedData.get("sysdate"), preparedAddtData);
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

                CurrTrans currTrans = this.currTransService.saveTransaction(preparedData, preparedAddtData, true);
                LOGGER.info(
                        String.format(
                                "Showing all variable value: \n< ALL SET VARIABLES OF CURR TRANS > \n%s",
                                currTrans.toString()
                        )
                );

            }
        } catch (Exception e) {
            LOGGER.info(String.format("Error: %s", e.getMessage()));
            LOGGER.error("", e);
        }
    }
}
