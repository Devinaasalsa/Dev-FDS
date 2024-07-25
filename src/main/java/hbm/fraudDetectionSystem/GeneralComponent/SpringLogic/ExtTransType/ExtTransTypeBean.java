package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtTransType;

import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfigurationRepository;
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
public class ExtTransTypeBean {
    private final Logger LOGGER = LOGGER(this.getClass().getName());
    protected final ExtTransTypeService extTransTypeService;
    protected final MessageConfigurationRepository messageConfigurationRepository;

    @Autowired
    public ExtTransTypeBean(ExtTransTypeService extTransTypeService, MessageConfigurationRepository messageConfigurationRepository) {
        this.extTransTypeService = extTransTypeService;
        this.messageConfigurationRepository = messageConfigurationRepository;
    }

    @Bean
    public Map<Long, List<ExtTransType>> extTransType() {
        LOGGER.info("[FETCHING EXTERNAL TRANS TYPE]");

        try {
            //String = Formatter
            Map<Long, List<ExtTransType>> fixedData = new LinkedHashMap<>();
            List<MessageConfiguration> msgConfigs = messageConfigurationRepository.findAll();
            List<ExtTransType> fetchedData = extTransTypeService.fetchAllData();

            msgConfigs
                    .forEach(v1 -> {
                        List<ExtTransType> dataFilterByConfigId = fetchedData
                                .stream()
                                .filter(data -> Objects.equals(data.getConfigId().getConfigId(), v1.getConfigId()))
                                .collect(Collectors.toList());
                        if (dataFilterByConfigId.size() > 0) {
                            fixedData.put(
                                    v1.getConfigId(),
                                    dataFilterByConfigId
                            );

                            LOGGER.debug(
                                    String.format(
                                            "Success fetch internal trans type with config id: %s and total configuration: %d",
                                            v1.getConfigId(),
                                            fixedData.get(v1.getConfigId()).size()
                                    )
                            );
                        } else {
                            LOGGER.info(
                                    String.format(
                                            "Config id: %s not have internal trans type, skipping...",
                                            v1.getConfigId()
                                    )
                            );
                        }
                    });

            if (fixedData.size() > 0) {
                LOGGER.info("Success fetch internal trans type");
            } else {
                LOGGER.warn("[THERE IS NO DATA FOR TRANS MSG CONFIGURATION]");
            }
            return fixedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN FETCH EXTERNAL TRANS TYPE]");
            return new LinkedHashMap<>();
        } finally {
            LOGGER.info("[FETCH EXTERNAL TRANS TYPE FINISHED]");
        }
    }
}
