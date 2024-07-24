package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core;

import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration.ChannelConfiguration;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.AllSequences.AllSequencesService;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrAddtTrans.CurrAddtTrans;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrAddtTrans.CurrAddtTransService;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans.CurrTrans;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans.CurrTransServiceImpl;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtRespCode.ExtRespCode;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtTransType.ExtTransType;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransTypeTab.TransTypeTab;
import hbm.fraudDetectionSystem.GeneralComponent.Utility.ApplicationContext;
import hbm.fraudDetectionSystem.GeneralComponent.Utility.DateHelper;
import hbm.fraudDetectionSystem.GeneralComponent.Utility.SequenceUpdaterService;
import hbm.fraudDetectionSystem.TransactionEngine.Core.Fraud.FraudDetection;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOMsg;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.SupportedMTI.SupportedMTI;
import hbm.fraudDetectionSystem.TransactionEngine.Exception.TransactionEngineException;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransDataAttribute.TransDataAttribute;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransMsgCfg.TransMsgCfg;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.getMTIConfiguration;

@Slf4j
public class RequestListener {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final Object dumpLock = new Object();
    protected final CurrTransServiceImpl currTransService;
    protected final AllSequencesService allSequencesService;
    protected final CurrAddtTransService currAddtTransService;
    protected final FraudDetection fraudDetection;
    protected final Map<String, List<TransDataAttribute>> isoTransDataAttributes;
    protected final Map<String, Map<String, List<TransMsgCfg>>> transMsgConfigs;
    protected final Map<Long, List<ExtTransType>> extTransType;
    protected final Map<Long, List<ExtRespCode>> extRespCodes;


    @SuppressWarnings("unchecked")
    public RequestListener() {
        currTransService = ApplicationContext.getBean("currTransServiceImpl", CurrTransServiceImpl.class);
        allSequencesService = ApplicationContext.getBean("allSequencesService", AllSequencesService.class);
        currAddtTransService = ApplicationContext.getBean("currAddtTransService", CurrAddtTransService.class);
        fraudDetection = ApplicationContext.getBean("fraudDetection", FraudDetection.class);
        isoTransDataAttributes = ApplicationContext.getBean("isoTransDataAttributes", Map.class);
        transMsgConfigs = ApplicationContext.getBean("transMsgConfigs", Map.class);
        extTransType = ApplicationContext.getBean("extTransType", Map.class);
        extRespCodes = ApplicationContext.getBean("extRespCodes", Map.class);
    }

    public ISOMsg process(ChannelConfiguration cc, ISOMsg m) throws ISOException {
        LOGGER.info("Receive data from [Channel Engine]");
        //Prepare Response Message Container
        ISOMsg respMsg = new ISOMsg();
        respMsg.setContainer(m.getContainer());

        Map<String, String> preparedData = new HashMap<>();
        List<CurrAddtTrans> preparedAddtData = new ArrayList<>();

        try {
            String configId = getConfigId(cc);
            Map<String, List<TransMsgCfg>> msgConfig = transMsgConfigs.get(configId);

            //Fetch and update UTRNNO
            long utnrno = allSequencesService.updateSeqNumber(SequenceUpdaterService.nextSeq(), 1);
            preparedData.put("utrnno", Long.toString(utnrno));

            //Generate sysDate
            preparedData.put("sysdate", DateHelper.convertTimestamp(new Timestamp(System.currentTimeMillis()).toString()));

            //Set PID
            preparedData.put("pid", getPID(cc));

            //Set ConfigId
            preparedData.put("configId", configId);

            //Prepare static data
            LOGGER.info("Start mapping value to trans attribute...");
            setPreparedData(m, preparedData, preparedAddtData, configId);

            //Set MTI
            String MTI = preparedData.get("mti");
            SupportedMTI mti = getMTIConfiguration(MTI);
            preparedData.put("isReversal", mti.getIsReversal().toString());
            preparedData.put("mti", mti.getValue());
            preparedData.put("mtiResp", mti.getRespValue());

            //Set Transaction Type
//            channelType = preparedData.get("merchantType");
            TransTypeTab convertedTransType = this.retrieveTransTypeFromMapping(preparedData.get("prcCode"), preparedData.get("transferIndicator"), cc.getMsgConfig().getConfigId(), this.extTransType);
            preparedData.put("transType", convertedTransType.getCode());
            preparedData.put("transTypeDesc", convertedTransType.getDescription());

            LOGGER.info("Send data to [Fraud Detection]");
            String intRespCode = this.validateResponseCode(
                    fraudDetection.detectTransaction(preparedData)
            );
            LOGGER.info("Receive data from [Fraud Detection]");

            ExtRespCode respCode = this.retrieveRespCodeFromMapping(intRespCode, cc.getMsgConfig().getConfigId(), this.extRespCodes);

            //Set Response Code
            preparedData.put("extRespCode", respCode.getRespCode());
            preparedData.put("respCode", respCode.getIntResp().getCode());
            preparedData.put("respCodeDesc", respCode.getIntResp().getDescription());

            this.reviewPreparedData(preparedData);

            //Save Curr Trans Data
            CurrTrans currTrans = currTransService.saveTransaction(preparedData, preparedAddtData, true);
            LOGGER.info(
                    String.format(
                            "Showing all variable value: \n< ALL SET VARIABLES OF CURR TRANS > \n%s",
                            currTrans.toString()
                    )
            );

            //Checking Transaction Message Config by configId
            if (msgConfig.size() > 0) {
                for (TransMsgCfg transMsgCfg : msgConfig.get(convertedTransType.getCode())) {
                    switch (transMsgCfg.getDer().getNotation()) {
                        case "m":
                            if (transMsgCfg.getFormat() != null)
                                respMsg.set(transMsgCfg.getFld(), DateTimeFormatter.ofPattern(transMsgCfg.getFormat()).format(LocalDateTime.now()));
                            else {
                                String value = preparedData.get(transMsgCfg.getTransAttr().getAttribute());
                                respMsg.set(transMsgCfg.getFld(), value);
                            }
                            break;

                        case "c":
                        case "o":
                            String value = preparedData.get(transMsgCfg.getTransAttr().getAttribute());
                            if (!value.isEmpty()) {
                                respMsg.set(transMsgCfg.getFld(), value);
                            }
                            break;

                        default:
                            break;
                    }
                }
            } else {
                throw new TransactionEngineException("This config id not having any transaction message configuration");
            }
        } catch (Exception e) {
            String mti = "0" + (Integer.parseInt(m.getString("0")) + 10);
            respMsg.set(0, mti);
            respMsg.set(39, "55");

            LOGGER.error(
                    String.format(
                            "Internal error: %s",
                            e.getMessage()
                    ),
                    1
            );

            LOGGER.error("", e);
        } finally {
            LOGGER.info("Send data to [Channel Engine]");
        }
        return respMsg;
    }

    protected ExtRespCode retrieveRespCodeFromMapping(String responseCode, long formatter, Map<Long, List<ExtRespCode>> extRespCodes) throws TransactionEngineException {
        LOGGER.info(
                String.format(
                        "Checking responseCode: %s with formatter: %s",
                        responseCode, formatter
                )
        );

        List<ExtRespCode> filteredRespCode = extRespCodes.get(formatter);
        if (filteredRespCode != null) {
            filteredRespCode = filteredRespCode
                    .stream()
                    .filter(data -> Objects.equals(data.getIntResp().getCode(), responseCode))
                    .collect(Collectors.toList());
            if (filteredRespCode.isEmpty()) {
                throw new TransactionEngineException("This iso response code not have any response code configuration, please check configuration!!!");
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

    protected TransTypeTab retrieveTransTypeFromMapping(String isoTransType, String transferIndicator, long configId, Map<Long, List<ExtTransType>> extTransType) throws TransactionEngineException {
        LOGGER.info(
                String.format(
                        "Checking iso trans type: %s with configId: %s",
                        isoTransType, configId
                )
        );

        List<ExtTransType> filteredTransType = extTransType.get(configId);

        if (filteredTransType != null) {
            //TODO::Ini harus nya di check dulu berdasarkan ChannelType (ATM,Mobile Banking, InetBanking)
            filteredTransType = filteredTransType
                    .stream()
                    .filter(data -> {
                        if (!transferIndicator.isEmpty()) {
                            return Objects.equals(data.getTransType().substring(0, 2), isoTransType.substring(0, 2)) && transferIndicator.equals(data.getTransType().substring(data.getTransType().length() - 1));
                        }
                        return Objects.equals(data.getTransType().substring(0, 2), isoTransType.substring(0, 2));
                    })
                    .collect(Collectors.toList());
            if (filteredTransType.isEmpty()) {
                throw new TransactionEngineException("This iso trans type not have any transaction type configuration, please check configuration!!!");
            }

            //Checking Transfer Indicator
            String transType = filteredTransType.get(0).getTransType();
            if (transType.contains("|")) {
                String temp = transType.substring(transType.length() - 1);

                if (!temp.equalsIgnoreCase(transferIndicator)) {
                    throw new TransactionEngineException("This iso trans type not have any transaction type configuration, please check configuration!!!");
                }
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

    protected void setPreparedData(ISOMsg m, Map<String, String> preparedData, List<CurrAddtTrans> preparedAddtData, String configId) throws TransactionEngineException {
        List<TransDataAttribute> listAttrId = isoTransDataAttributes.get(configId);
        if (listAttrId != null) {
            for (TransDataAttribute attr : listAttrId) {
                String fieldTag = retrieveAttrFieldTag(attr);
                if (fieldTag != null) {
                    String value = convertValue(m.getString(fieldTag));
                    preparedData.put(attr.getAttribute(), value == null ? "" : value.trim());

                    if (attr.getAddtData() && value != null) {
                        CurrAddtTrans currAddtTrans = CurrAddtTrans.builder()
                                .attr(attr.getAttribute())
                                .description(attr.getDescription())
                                .value(value.trim()).build();
                        preparedAddtData.add(currAddtTrans);
                    }
//                    evt.addMessage(String.format("Value: %s", value != null ? value : ""), 1);
                }
            }
        } else
            throw new TransactionEngineException("Data attributes isn't found");
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
        return attr.getConfigId() != null;
    }

    protected String convertValue(Object value) {
        return value instanceof ISOMsg ? ((ISOMsg) value).getParentFieldNumberValue().toString() :
                value != null ? value.toString() : null;
    }

    protected String getConfigId(ChannelConfiguration cc) {
        return cc.getMsgConfig().getConfigId().toString();
    }

    protected String getPID(ChannelConfiguration cc) {
        return cc.getPid().toString();
    }

    protected String validateResponseCode(String temp) {
        return temp != null ?
                temp : "-1";
    }
}