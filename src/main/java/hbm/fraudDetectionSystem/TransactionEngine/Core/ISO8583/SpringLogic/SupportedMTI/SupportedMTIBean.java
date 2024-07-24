package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.SupportedMTI;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import java.util.LinkedHashMap;
import java.util.Map;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.LIST_MTI;
import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.LOGGER;
import static hbm.fraudDetectionSystem.GeneralComponent.Utility.SpringLogicHelper.*;

@Configuration(proxyBeanMethods = false)
public class SupportedMTIBean {
    private final Logger LOGGER = LOGGER(this.getClass().getName());
    private final SupportedMTIRepository supportedMTIRepository;
    protected final Environment env;

    @Autowired
    public SupportedMTIBean(SupportedMTIRepository supportedMTIRepository, Environment env) {
        this.supportedMTIRepository = supportedMTIRepository;
        this.env = env;
    }

    @Bean
    public Map<String, SupportedMTI> listSupportedMTI() {
        LOGGER.info("[FETCHING SUPPORTED MTI]");

        try {
            Map<String, SupportedMTI> fixedData = new LinkedHashMap<>();
            supportedMTIRepository.findAllByOrderByIdAsc().forEach(
                    data -> fixedData.put(data.getValue(), data)
            );

            if (fixedData.size() == 0) {
                LOGGER.warn("[THERE IS NO DATA FOR SUPPORTED MTI]");
            } else {
                LIST_MTI.putAll(fixedData);
                LOGGER.info(
                        String.format(
                                "Success fetch MTI, with total MTI: %d",
                                fixedData.size()
                        )
                );
            }

            return fixedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN FETCH SUPPORTED MTI]");
            return new LinkedHashMap<>();
        } finally {
            LOGGER.info("[FETCH SUPPORTED MTI FINISHED]");
        }
    }
}
