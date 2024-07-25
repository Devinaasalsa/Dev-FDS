package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtRespCode;

import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfigurationRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.LOGGER;

@Configuration(proxyBeanMethods = false)
public class ExtRespCodeBean {
    private final Logger LOGGER = LOGGER(this.getClass().getName());
    protected final ExtRespCodeService extRespCodeService;
    protected final MessageConfigurationRepository messageConfigurationRepository;

    @Autowired
    public ExtRespCodeBean(ExtRespCodeService extRespCodeService, MessageConfigurationRepository messageConfigurationRepository) {
        this.extRespCodeService = extRespCodeService;
        this.messageConfigurationRepository = messageConfigurationRepository;
    }

    @Bean
    public Map<Long, List<ExtRespCode>> extRespCodes() {
        LOGGER.info("[FETCHING EXTERNAL RESP CODE]");

        try {
            //String = Formatter
            Map<Long, List<ExtRespCode>> fixedData = new LinkedHashMap<>();
            List<MessageConfiguration> msgConfigs = messageConfigurationRepository.findAll();
            List<ExtRespCode> fetchedData = extRespCodeService.fetchAllData();
            msgConfigs
                    .forEach(v1 -> {
                        List<ExtRespCode> dataFilterByConfigId = fetchedData
                                .stream()
                                .filter(data -> data.getConfigId().getConfigId().equals(v1.getConfigId()))
                                .collect(Collectors.toList());
                        if (dataFilterByConfigId.size() > 0) {
                            fixedData.put(
                                    v1.getConfigId(),
                                    dataFilterByConfigId
                            );

                            LOGGER.debug(
                                    String.format(
                                            "Success fetch internal response with config id: %s and total configuration: %d",
                                            v1.getConfigId(),
                                            fixedData.get(v1.getConfigId()).size()
                                    )
                            );
                        } else {
                            LOGGER.info(
                                    String.format(
                                            "Config id: %s not have internal response, skipping...",
                                            v1.getConfigId()
                                    )
                            );
                        }
                    });
            // Log info for successful fetch of internal trans types
            LOGGER.info(
                    String.format(
                            "Success fetch internal response code",
                            fixedData.size()
                    )
            );

            return fixedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN FETCH EXTERNAL RESP CODE]");
            return new LinkedHashMap<>();
        } finally {
            LOGGER.info("[FETCH EXTERNAL RESP CODE FINISHED]");
        }
    }
}
