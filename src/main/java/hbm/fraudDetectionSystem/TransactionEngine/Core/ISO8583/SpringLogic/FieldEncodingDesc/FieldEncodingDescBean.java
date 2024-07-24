package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldEncodingDesc;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.LOGGER;

@Configuration(proxyBeanMethods = false)
public class FieldEncodingDescBean {
    private final Logger LOGGER = LOGGER(this.getClass().getName());
    private final FieldEncodingDescService fieldEncodingDescService;

    @Autowired
    public FieldEncodingDescBean(FieldEncodingDescService fieldEncodingDescService) {
        this.fieldEncodingDescService = fieldEncodingDescService;
    }

    @Bean
    public Map<Long, FieldEncodingDesc> fieldEncoding() {
        LOGGER.info("[FETCHING FIELD ENCODING]");
        try {
            Map<Long, FieldEncodingDesc> fixedData = new LinkedHashMap<>();
            fieldEncodingDescService.findAllData()
                    .forEach(fetchedData -> fixedData.put(fetchedData.getEncodingId(), fetchedData));


            LOGGER.info(
                    String.format(
                            "Success fetch field encoding with total encoding: %d",
                            fixedData.size()
                    )
            );
            return fixedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN FETCH FIELD ENCODING]");
            return new LinkedHashMap<>();
        } finally {
            LOGGER.info("[FETCH FIELD ENCODING FINISHED]");
        }
    }
}
