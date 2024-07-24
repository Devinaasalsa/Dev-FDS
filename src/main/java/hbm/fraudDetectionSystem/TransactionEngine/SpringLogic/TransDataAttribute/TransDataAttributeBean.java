package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransDataAttribute;

import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint.ChannelEndpoint;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.LOGGER;

@Configuration(proxyBeanMethods = false)
public class TransDataAttributeBean {
    private final Logger LOGGER = LOGGER(this.getClass().getName());
    protected final TransDataAttributeService transDataAttributeService;

    @Autowired
    public TransDataAttributeBean(TransDataAttributeService transDataAttributeService) {
        this.transDataAttributeService = transDataAttributeService;
    }

    @Bean(name = "isoTransDataAttributes")
    public Map<String, List<TransDataAttribute>> isoTransDataAttributes(Map<String, MessageConfiguration> isoConfigurations) {
        LOGGER.info("[FETCHING TRANS ATTR]");

        try {
            //String = ConfigId
            Map<String, List<TransDataAttribute>> fixedData = new LinkedHashMap<>();
            isoConfigurations
                    .forEach((configId, isoConfiguration) -> {
                        List<TransDataAttribute> fetchedData = transDataAttributeService.fetchAllByConfigId(Long.parseLong(configId));
                        if (fetchedData.size() > 0) {
                            fixedData.put(
                                    configId,
                                    fetchedData
                            );

                            LOGGER.info(
                                    String.format(
                                            "Success fetch trans attr with config id: %s and total configuration: %d",
                                            configId,
                                            fixedData.get(configId).size()
                                    )
                            );
                        } else {
                            LOGGER.info(
                                    String.format(
                                            "Config id: %s not have trans attr, skipping...",
                                            configId
                                    )
                            );
                        }
                    });

            LOGGER.info("Success fetch trans attr");

            return fixedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN FETCH TRANS ATTR]");
            return new LinkedHashMap<>();
        } finally {
            LOGGER.info("[FETCH TRANS ATTR FINISHED]");
        }
    }

    @Bean(name = "jsonTransDataAttributes")
    public Map<String, Map<String, List<TransDataAttribute>>> jsonTransDataAttributes(Map<String, MessageConfiguration> isoConfigurations) {
        LOGGER.info("[FETCHING JSON TRANS ATTR]");

        try {
            /*
                String = ConfigId
                String = Url
             */
            Map<String, Map<String, List<TransDataAttribute>>> fixedData = new LinkedHashMap<>();
            isoConfigurations
                    .forEach((configId, isoConfiguration) -> {
                        Map<String, List<TransDataAttribute>> attrByEndpoint = new LinkedHashMap<>();

                        for (ChannelEndpoint ce : isoConfiguration.getEndpoints()) {
                            List<TransDataAttribute> fetchedData = transDataAttributeService.fetchAllByConfigIdAndEndpointId(isoConfiguration.getConfigId(), ce.getEndpointId());
                            if (fetchedData.size() > 0) {
                                attrByEndpoint.put(
                                        ce.getUrl(),
                                        fetchedData
                                );

                                LOGGER.info(
                                        String.format(
                                                "Success fetch trans attr with config id: %s, endpoint id: %s and total configuration: %d",
                                                configId,
                                                ce.getEndpointId(),
                                                attrByEndpoint.get(ce.getUrl()).size()
                                        )
                                );
                            } else {
                                LOGGER.info(
                                        String.format(
                                                "Config id: %s, Endpoint id: %s not have trans attr, skipping...",
                                                configId, ce.getEndpointId()
                                        )
                                );
                            }
                        }

                        fixedData.put(
                                configId,
                                attrByEndpoint
                        );
                    });
            LOGGER.info(
                    String.format(
                            "Success fetch trans attr"
                    )
            );
            return fixedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN FETCH JSON TRANS ATTR]");
            return new LinkedHashMap<>();
        } finally {
            LOGGER.info("[FETCH JSON TRANS ATTR FINISHED]");
        }
    }
}
