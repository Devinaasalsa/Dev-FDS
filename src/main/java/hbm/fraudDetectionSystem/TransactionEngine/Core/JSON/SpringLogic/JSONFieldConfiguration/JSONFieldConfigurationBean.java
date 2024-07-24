package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldConfiguration;

import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration.ChannelConfiguration;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint.ChannelEndpoint;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint.ChannelEndpointRepository;
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
public class JSONFieldConfigurationBean {
    private final Logger LOGGER = LOGGER(this.getClass().getName());
    private final JSONFieldConfigurationRepository jsonFieldConfigurationRepository;

    @Autowired
    public JSONFieldConfigurationBean(JSONFieldConfigurationRepository jsonFieldConfigurationRepository) {
        this.jsonFieldConfigurationRepository = jsonFieldConfigurationRepository;
    }

    @Bean(name = "jsonFieldConfigurations")
    public Map<String, Map<String, Map<Integer, List<JSONFieldConfiguration>>>> jsonFieldConfigurations(List<ChannelConfiguration> jsonChannelConfigurations) {
        LOGGER.info("[FETCHING JSON FIELD CONFIGURATION]");

        try {
            /*
                String = Base endpoint
                String = url
                Integer = State (Req / Resp),
                String = Field Name
             */

            Map<String, Map<String, Map<Integer, List<JSONFieldConfiguration>>>> fixedData = new LinkedHashMap<>();
            for (ChannelConfiguration cc : jsonChannelConfigurations) {
                Map<String, Map<Integer, List<JSONFieldConfiguration>>> configByUrl = new LinkedHashMap<>();

                for (ChannelEndpoint ce : cc.getMsgConfig().getEndpoints()) {
                    List<JSONFieldConfiguration> parentMapReq = new LinkedList<>();
                    List<JSONFieldConfiguration> parentMapResp = new LinkedList<>();

                    List<JSONFieldConfiguration> fetchParent =
                            jsonFieldConfigurationRepository.findParentConfigurationByMsgType(
                                    cc.getMsgConfig().getConfigId(),
                                    ce.getEndpointId(),
                                    2L
                            );

                    if (!fetchParent.isEmpty()) {
                        for (JSONFieldConfiguration parentConfig : fetchParent) {
                            List<JSONFieldConfiguration> fetchChild =
                                    jsonFieldConfigurationRepository.findChildConfigurationByMsgType(
                                            cc.getMsgConfig().getConfigId(),
                                            ce.getEndpointId(),
                                            2L,
                                            parentConfig.getId()
                                    );

                            if (!fetchChild.isEmpty()) {
                                parentConfig.setChildField(fetchChild);
                            }

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
                                "Success fetch field configuration with base endpoint: %s",
                                cc.getConnectionConfig().getBaseEndpoint()
                        )
                );


            }

            if (fixedData.size() > 0) {
                LOGGER.info(
                    String.format(
                            "Success fetch field configuration with base endpoint"
                    )
                    );
            } else {
                LOGGER.warn("[THERE IS NO DATA FOR JSON FIELD CONFIGURATION]");
            }

            return fixedData;

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN FETCH JSON FIELD CONFIGURATION]");
            return new LinkedHashMap<>();
        } finally {
            LOGGER.info("[FETCH JSON FIELD CONFIGURATION FINISHED]");
        }
    }
}
