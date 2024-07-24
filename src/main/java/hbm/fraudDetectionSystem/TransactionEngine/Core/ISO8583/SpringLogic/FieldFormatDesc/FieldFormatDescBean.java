package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldFormatDesc;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.LOGGER;

@Configuration(proxyBeanMethods = false)
public class FieldFormatDescBean {
    private final Logger LOGGER = LOGGER(this.getClass().getName());
    private final FieldFormatDescRepository fieldFormatDescRepository;

    @Autowired
    public FieldFormatDescBean(FieldFormatDescRepository fieldFormatDescRepository) {
        this.fieldFormatDescRepository = fieldFormatDescRepository;
    }

    @Bean
    public Map<Long, FieldFormatDesc> fieldFormat() {
        LOGGER.info("[FETCHING FIELD FORMAT]");

        try {
            Map<Long, FieldFormatDesc> fixedData = new LinkedHashMap<>();
            fieldFormatDescRepository.findAllByOrderByFormatIdAsc()
                    .forEach(fetchedData -> fixedData.put(fetchedData.getFormatId(), fetchedData));


            LOGGER.info(
                    String.format(
                            "Success fetch field format with total format: %d",
                            fixedData.size()
                    )
            );

            return fixedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN FETCH FIELD FORMAT]");
            return new LinkedHashMap<>();
        } finally {
            LOGGER.info("[FETCH FIELD FORMAT FINISHED]");
        }
    }
}
