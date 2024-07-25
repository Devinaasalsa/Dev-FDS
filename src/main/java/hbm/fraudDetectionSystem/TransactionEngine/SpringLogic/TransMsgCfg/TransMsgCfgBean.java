package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransMsgCfg;

import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransTypeTab.TransTypeTab;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransTypeTab.TransTypeTabRepository;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransTypeDesc.TransTypeDesc;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.LOGGER;

@Configuration(proxyBeanMethods = false)
public class TransMsgCfgBean {
    private final Logger LOGGER = LOGGER(this.getClass().getName());
    protected final TransMsgCfgService transMsgCfgService;
    protected final TransTypeTabRepository transTypeTabRepository;

    @Autowired
    public TransMsgCfgBean(TransMsgCfgService transMsgCfgService, TransTypeTabRepository transTypeTabRepository) {
        this.transMsgCfgService = transMsgCfgService;
        this.transTypeTabRepository = transTypeTabRepository;
    }

//    @Bean(name = "transMsgConfigsTemp")
//    public Map<String, Map<String, List<TransMsgCfg>>> transMsgConfigsTemp(Map<String, MessageConfiguration> isoConfigurations, List<TransTypeDesc> transTypes) {
//        LOGGER.info("[FETCHING TRANS MSG CONFIGURATION]");
//        startupEngineLogging("[FETCHING TRANS MSG CONFIGURATION]");
//
//        try {
//            //Map<ConfigId, Map<TransType, List<TransMsgCfg>>>
//            Map<String, Map<String, List<TransMsgCfg>>> fixedData = new LinkedHashMap<>();
//            isoConfigurations
//                    .forEach((configId, isoConfiguration) -> {
//                        List<TransMsgCfg> filteredByConfigId = transMsgCfgService.fetchAllConfig()
//                                .stream()
//                                .filter(data -> data.getConfigId().getConfigId().toString().equals(configId))
//                                .collect(Collectors.toList());
//
//                        Map<String, List<TransMsgCfg>> filteredByTransType = new LinkedHashMap<>();
//
//                        transTypes.forEach(data -> {
//                            List<TransMsgCfg> dataByTransType = filteredByConfigId
//                                    .stream()
//                                    .filter(data1 -> Objects.equals(data1.getTransTypeTemp().getTypeId(), data.getTypeId()))
//                                    .collect(Collectors.toList());
//
//                            if (dataByTransType.size() > 1)
//                                filteredByTransType.put(
//                                        data.getCode(),
//                                        dataByTransType
//                                );
//                        });
//
//                        fixedData.put(configId, filteredByTransType);
//
//                        if (fixedData.get(configId) != null) {
//                            LOGGER.info(
//                                    String.format(
//                                            "Success fetch field configuration with network id: %s with total configuration: %d",
//                                            configId,
//                                            fixedData.get(configId).size()
//                                    )
//                            );
//                            startupEngineLogging(
//                                    String.format(
//                                            "Success fetch field configuration with network id: %s with total configuration: %d",
//                                            configId,
//                                            fixedData.get(configId).size()
//                                    )
//                            );
//                        }
//                    });
//
//            if (fixedData.size() == 0) {
//                LOGGER.warn("[THERE IS NO DATA FOR TRANS MSG CONFIGURATION]");
//                startupEngineLogging("[THERE IS NO DATA FOR TRANS MSG CONFIGURATION]");
//            }
//
//            return fixedData;
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage());
//            e.printStackTrace();
//            LOGGER.error("[ERROR WHEN FETCH TRANS MSG CONFIGURATION]");
//            startupEngineLogging("[ERROR WHEN FETCH TRANS MSG CONFIGURATION]");
//            return new LinkedHashMap<>();
//        } finally {
//            LOGGER.info("[FETCH TRANS MSG CONFIGURATION FINISHED]");
//            startupEngineLogging("[FETCH TRANS MSG CONFIGURATION FINISHED]");
//        }
//    }

    @Bean(name = "transMsgConfigs")
    public Map<String, Map<String, List<TransMsgCfg>>> transMsgConfigs(Map<String, MessageConfiguration> isoConfigurations) {
        LOGGER.info("[FETCHING TRANS MSG CONFIGURATION]");

        try {
            //Map<ConfigId, Map<TransType, List<TransMsgCfg>>>
            Map<String, Map<String, List<TransMsgCfg>>> fixedData = new LinkedHashMap<>();
            List<TransTypeTab> transTypes = this.transTypeTabRepository.findAll();

            isoConfigurations
                    .forEach((configId, isoConfiguration) -> {
                        List<TransMsgCfg> filteredByConfigId = transMsgCfgService.fetchAllConfig()
                                .stream()
                                .filter(data -> data.getConfigId().getConfigId().toString().equals(configId))
                                .collect(Collectors.toList());

                        Map<String, List<TransMsgCfg>> filteredByTransType = new LinkedHashMap<>();

                        transTypes.forEach(data -> {
                            List<TransMsgCfg> dataByTransType = filteredByConfigId
                                    .stream()
                                    .filter(data1 -> Objects.equals(data1.getTransType().getId(), data.getId()))
                                    .collect(Collectors.toList());

                            if (dataByTransType.size() > 0)
                                filteredByTransType.put(
                                        data.getCode(),
                                        dataByTransType
                                );
                        });

                        fixedData.put(configId, filteredByTransType);

                        if (fixedData.get(configId) != null) {
                            LOGGER.debug(
                                    String.format(
                                            "Success fetch field configuration with network id: %s with total configuration: %d",
                                            configId,
                                            fixedData.get(configId).size()
                                    )
                            );
                        }
                    });

            if (fixedData.size() > 0) {
                LOGGER.info("Success fetch field configuration [Trans Msg Configuration]");
            } else {
                LOGGER.warn("[THERE IS NO DATA FOR TRANS MSG CONFIGURATION]");
            }

            return fixedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN FETCH TRANS MSG CONFIGURATION]");
            return new LinkedHashMap<>();
        } finally {
            LOGGER.info("[FETCH TRANS MSG CONFIGURATION FINISHED]");
        }
    }
}
