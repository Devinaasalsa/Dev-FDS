package hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedList;
import java.util.List;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.LOGGER;

@Configuration(proxyBeanMethods = false)

public class ChannelConfigurationBean {
    private final Logger LOGGER = LOGGER(this.getClass().getName());
    protected final ChannelConfigurationRepository channelConfigurationRepository;

    @Autowired
    public ChannelConfigurationBean(ChannelConfigurationRepository channelConfigurationRepository) {
        this.channelConfigurationRepository = channelConfigurationRepository;
    }

    @Bean
    public List<ChannelConfiguration> isoChannelConfigurations() {
        LOGGER.info("[FETCHING CHANNEL CONFIGURATION]");
        try {
            List<ChannelConfiguration> fixedData = channelConfigurationRepository.findAllByMsgType(1);
            //String = channelTypeId

            if (fixedData.size() == 0) {
                LOGGER.warn("[THERE IS NO DATA FOR CHANNEL CONFIGURATION]");
            }

            return fixedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN FETCH CHANNEL CONFIGURATION]");
            return new LinkedList<>();
        } finally {
            LOGGER.info("[FETCH CHANNEL CONFIGURATION FINISHED]");
        }
    }

    @Bean
    public List<ChannelConfiguration> jsonChannelConfigurations() {
        LOGGER.info("[FETCHING JSON CHANNEL CONFIGURATION]");
        try {
            List<ChannelConfiguration> fixedData = channelConfigurationRepository.findAllByMsgType(2);
            //String = channelTypeId

            if (fixedData.size() == 0) {
                LOGGER.warn("[THERE IS NO DATA FOR JSON CHANNEL CONFIGURATION]");
            }

            return fixedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN FETCH JSON CHANNEL CONFIGURATION]");
            return new LinkedList<>();
        } finally {
            LOGGER.info("[FETCH JSON CHANNEL CONFIGURATION FINISHED]");
        }
    }
}
