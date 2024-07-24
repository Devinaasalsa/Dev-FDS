package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.LOGGER;

@Configuration(proxyBeanMethods = false)
public class MessageConfigurationBean {
    private final Logger LOGGER = LOGGER(this.getClass().getName());
    private final MessageConfigurationService messageConfigurationService;

    @Autowired
    public MessageConfigurationBean(MessageConfigurationService messageConfigurationService) {
        this.messageConfigurationService = messageConfigurationService;
    }

    @Bean
    public Map<String, MessageConfiguration> isoConfigurations() {
        LOGGER.info("[FETCHING ISO CONFIGURATION]");

        try {
            Map<String, MessageConfiguration> fixedData = new LinkedHashMap<>();
            for (MessageConfiguration messageConfiguration : messageConfigurationService.findAllData()) {
                fixedData.put(messageConfiguration.getConfigId().toString(), messageConfiguration);
            }

            if (fixedData.size() > 0) {
                LOGGER.info(
                        String.format("Success fetch iso configuration with total configuration: %d", fixedData.size())
                );
            } else {
                LOGGER.warn("[THERE IS NO DATA FOR ISO CONFIGURATION]");
            }


            return fixedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN FETCH ISO CONFIGURATION]");
            return new LinkedHashMap<>();
        } finally {
            LOGGER.info("[FETCH ISO CONFIGURATION FINISHED]");
        }
    }
}
