package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.HeaderConfiguration;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOFieldContainer;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.LOGGER;
import static hbm.fraudDetectionSystem.GeneralComponent.Utility.SpringLogicHelper.handlerMapper;

@Configuration(proxyBeanMethods = false)
public class HeaderConfigurationBean {
    private final Logger LOGGER = LOGGER(this.getClass().getName());
    private final HeaderConfigurationRepository HeaderConfigurationRepository;

    public HeaderConfigurationBean(HeaderConfigurationRepository HeaderConfigurationRepository) {
        this.HeaderConfigurationRepository = HeaderConfigurationRepository;
    }

    @Bean
    public Map<String, List<HeaderConfiguration>> headerConfigurations(Map<String, MessageConfiguration> isoConfigurations) {
        LOGGER.info("[FETCHING HEADER CONFIGURATION]");

        try {
            //String = NetworkId
            Map<String, List<HeaderConfiguration>> fixedData = new LinkedHashMap<>();
            isoConfigurations
                    .forEach((networkId, isoConfiguration) -> {
                        if (isoConfiguration.isHasHeader()) {
                            fixedData.put(
                                    networkId,
                                    HeaderConfigurationRepository.findAllByOrderByConfigIdAscFieldIdAscPriorityAsc()
                                            .stream()
                                            .filter(data -> data.getConfigId().getConfigId().toString().equals(networkId))
                                            .collect(Collectors.toList())
                            );
                            LOGGER.info(
                                    String.format(
                                            "Success fetch header configuration with network id: %s with total configuration: %d",
                                            networkId,
                                            fixedData.get(networkId).size()
                                    )
                            );
                        } else {
                            LOGGER.info(
                                    String.format(
                                            "Network id: %s not have header configuration, skipping...",
                                            networkId
                                    )
                            );
                        }
                    });

            if (fixedData.size() == 0) {
                LOGGER.warn("[THERE IS NO DATA FOR HEADER CONFIGURATION]");
            }

            return fixedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN FETCH HEADER CONFIGURATION]");
            return new LinkedHashMap<>();
        } finally {
            LOGGER.info("[FETCH HEADER CONFIGURATION FINISHED]");
        }
    }

    @Bean(name = "headerConfigurationContainer")
    public Map<String, ISOFieldContainer[]> headerConfigurationContainer(Map<String, List<HeaderConfiguration>> headerConfigurations) {
        LOGGER.info("[SETTING HEADER CONFIGURATION CONTAINER]");

        try {
            Map<String, ISOFieldContainer[]> fixedData = new LinkedHashMap<>();
            headerConfigurations.forEach((key, value) -> {
                ISOFieldContainer[] headerContainer = new ISOFieldContainer[value.size()];
                for (int i = 0; i < value.size(); i++) {
                    HeaderConfiguration HeaderConfiguration = value.get(i);
                    headerContainer[i] = handlerMapper(HeaderConfiguration.getEncodingId().getIntId(), HeaderConfiguration.getFormatId().getFormatId(), HeaderConfiguration.getFieldId(), HeaderConfiguration.getLength(), HeaderConfiguration.getDescription(), HeaderConfiguration.getCondId(), false);
                }

                fixedData.put(
                        key,
                        headerContainer
                );
            });

            if (fixedData.size() == 0) {
                LOGGER.warn("[THERE IS NO DATA FOR HEADER CONFIGURATION CONTAINER]");
            } else {
                LOGGER.info(String.format(
                        "Success fetch header configuration with total: %d",
                        fixedData.size()
                ));
            }

            return fixedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN SETTING HEADER CONFIGURATION CONTAINER]");
            return new LinkedHashMap<>();
        } finally {
            LOGGER.info("[SUCCESS SETTING HEADER CONFIGURATION CONTAINER]");
        }
    }
}
