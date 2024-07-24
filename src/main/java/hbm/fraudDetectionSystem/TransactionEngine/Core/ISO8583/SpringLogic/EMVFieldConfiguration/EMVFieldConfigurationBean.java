package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.EMVFieldConfiguration;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.LOGGER;

@Configuration(proxyBeanMethods = false)
public class EMVFieldConfigurationBean {
    private final Logger LOGGER = LOGGER(this.getClass().getName());
    private final EMVFieldConfigurationService emvFieldConfigurationService;

    @Autowired
    public EMVFieldConfigurationBean(EMVFieldConfigurationService emvFieldConfigurationService) {
        this.emvFieldConfigurationService = emvFieldConfigurationService;
    }

    @Bean
    public Map<String, EMVFieldConfiguration> emvConfigurations() {
        LOGGER.info("[FETCHING EMV CONFIGURATION]");

        try {
            /*
                Integer = ID of EMV field
                String = EMV TAG ID
             */
            Map<String, EMVFieldConfiguration> fixedData = new LinkedHashMap<>();
            List<EMVFieldConfiguration> fetchedData = emvFieldConfigurationService.findAllData();
            for (EMVFieldConfiguration emvFieldConfiguration : fetchedData) {
                fixedData.put(emvFieldConfiguration.getEmvTagId(), emvFieldConfiguration);
            }

            if (fixedData.size() == 0) {
                LOGGER.warn("[THERE IS NO DATA FOR EMV CONFIGURATION]");
            } else {
                LOGGER.info(
                        String.format(
                                "Success fetch emv configuration with total configuration: %d",
                                fetchedData.size()
                        )
                );
            }

            return fixedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());

            e.printStackTrace();

            LOGGER.error("[ERROR WHEN FETCH EMV CONFIGURATION]");
            return new LinkedHashMap<>();
        } finally {
            LOGGER.info("[FETCHING EMV CONFIGURATION FINISHED]");
        }
    }
}
