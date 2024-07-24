package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONHeaderConfiguration;

import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration.ChannelConfiguration;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint.ChannelEndpoint;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.LOGGER;

@Configuration(proxyBeanMethods = false)
public class JSONHeaderConfigurationBean {
    private final Logger LOGGER = LOGGER(this.getClass().getName());

    protected final JSONHeaderConfigurationRepository jsonHeaderConfigurationRepository;

    @Autowired
    public JSONHeaderConfigurationBean(JSONHeaderConfigurationRepository jsonHeaderConfigurationRepository) {
        this.jsonHeaderConfigurationRepository = jsonHeaderConfigurationRepository;
    }

    @Bean(name = "jsonHeaderConfigurations")
    public Map<String, Map<String, Map<Integer, List<JSONHeaderConfiguration>>>> jsonHeaderConfigurations(List<ChannelConfiguration> jsonChannelConfigurations) {
        LOGGER.info("[FETCHING JSON HEADER CONFIGURATION]");

        try {
            /*
                String = Base endpoint
                String = url
                Integer = State (Req / Resp),
                String = Field Name
             */

            Map<String, Map<String, Map<Integer, List<JSONHeaderConfiguration>>>> fixedData = new LinkedHashMap<>();
            for (ChannelConfiguration cc : jsonChannelConfigurations) {
                Map<String, Map<Integer, List<JSONHeaderConfiguration>>> configByUrl = new LinkedHashMap<>();

                for (ChannelEndpoint ce : cc.getMsgConfig().getEndpoints()) {
                    List<JSONHeaderConfiguration> parentMapReq = new LinkedList<>();
                    List<JSONHeaderConfiguration> parentMapResp = new LinkedList<>();

                    List<JSONHeaderConfiguration> fetchParent =
                            jsonHeaderConfigurationRepository.findHeaderConfigurationByMsgType(
                                    cc.getMsgConfig().getConfigId(),
                                    ce.getEndpointId(),
                                    2L
                            );

                    if (!fetchParent.isEmpty()) {
                        for (JSONHeaderConfiguration parentConfig : fetchParent) {
                            switch (parentConfig.getState()) {
                                case REQUEST:
                                    parentMapReq.add(parentConfig);
                                    break;

                                case RESPONSE:
                                    parentMapResp.add(parentConfig);
                                    break;

                                default:
                                    LOGGER.error("Unknown state");
                                    break;
                            }
                        }

                        configByUrl.put(
                                ce.getUrl(),
                                Map.of(
                                        1, parentMapReq,
                                        2, parentMapResp
                                )
                        );
                    }
                }
                fixedData.put(
                        cc.getMsgConfig().getConfigId() + "|" + cc.getConnectionConfig().getBaseEndpoint(),
                        configByUrl
                );

                LOGGER.info(
                        String.format(
                                "Success fetch json header configuration with base endpoint: %s",
                                cc.getConnectionConfig().getBaseEndpoint()
                        )
                );

            }

            if (fixedData.size() > 0) {
                LOGGER.info(
                            "Success fetch json header configuration with base endpoint"
            );
            } else {
                LOGGER.warn("[THERE IS NO DATA FOR JSON HEADER CONFIGURATION]");
            }

            return fixedData;

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN FETCH JSON HEADER CONFIGURATION]");
            return new LinkedHashMap<>();
        } finally {
            LOGGER.info("[FETCH JSON HEADER CONFIGURATION FINISHED]");
        }
    }
}
