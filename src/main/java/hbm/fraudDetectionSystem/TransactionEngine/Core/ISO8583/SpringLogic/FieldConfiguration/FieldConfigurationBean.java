package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldConfiguration;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.*;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.ascii.A_BITMAP;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler.EMVDataHandler;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.EMVFieldConfiguration.EMVFieldConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.SubFieldConfiguration.SubFieldConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.LOGGER;
import static hbm.fraudDetectionSystem.GeneralComponent.Utility.SpringLogicHelper.handlerMapper;

@Configuration(proxyBeanMethods = false)
public class FieldConfigurationBean {
    private final Logger LOGGER = LOGGER(this.getClass().getName());
    private final FieldConfigurationRepository fieldConfigurationRepository;

    @Autowired
    public FieldConfigurationBean(FieldConfigurationRepository fieldConfigurationRepository) {
        this.fieldConfigurationRepository = fieldConfigurationRepository;
    }

    @Bean(name = "fieldConfigurations")
    public Map<String, List<FieldConfiguration>> fieldConfigurations(Map<String, MessageConfiguration> isoConfigurations) {
        LOGGER.info("[FETCHING FIELD CONFIGURATION]");

        try {
            //String = NetworkId
            Map<String, List<FieldConfiguration>> fixedData = new LinkedHashMap<>();
            isoConfigurations
                    .forEach((networkId, isoConfiguration) -> {
                        fixedData.put(
                                networkId,
                                fieldConfigurationRepository.findAllByOrderByConfigIdAscFieldIdAscPriorityAsc()
                                        .stream()
                                        .filter(data -> data.getConfigId().getConfigId().toString().equals(networkId))
                                        .collect(Collectors.toList())
                        );
                        LOGGER.debug(
                                String.format(
                                        "Success fetch field configuration with network id: %s with total configuration: %d",
                                        networkId,
                                        fixedData.get(networkId).size()
                                )
                        );
                    }

                    );

            if (fixedData.size() > 0) {
                LOGGER.info("Success fetch field configuration");
            } else {
                LOGGER.warn("[THERE IS NO DATA FOR FIELD CONFIGURATION]");
            }

            return fixedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN FETCH FIELD CONFIGURATION]");
            return new LinkedHashMap<>();
        } finally {
            LOGGER.info("[FETCH FIELD CONFIGURATION FINISHED]");
        }
    }

    @Bean(name = "fieldConfigurationContainer")
    public Map<String, ISOFieldContainer[]> fieldConfigurationContainer(Map<String, List<FieldConfiguration>> fieldConfigurations, Map<String, EMVFieldConfiguration> emvConfigurations) {
        LOGGER.info("[SETTING ISO CONFIGURATION CONTAINER]");

        try {
            Map<String, ISOFieldContainer[]> fixedData = new LinkedHashMap<>();
            fieldConfigurations.forEach((key, value) -> {
                ISOFieldContainer[] container = new ISOFieldContainer[fieldConfigurations.get(key).size()];
                try {
                    for (int i = 0; i < value.size(); i++) {
                        FieldConfiguration fieldConfiguration = value.get(i);
                        if (fieldConfiguration.getHasChild()) {
                            int j = 0;
                            Boolean isTlv = false;
                            ISOFieldContainer[] subContainer = new ISOFieldContainer[fieldConfiguration.getSubFieldConfigurations().size()];
                            for (SubFieldConfiguration subFieldConfiguration : fieldConfiguration.getSubFieldConfigurations()) {
                                subContainer[j] =
                                        handlerMapper(subFieldConfiguration.getEncodingId().getEncodingId().intValue(), subFieldConfiguration.getFormatId().getFormatId(), subFieldConfiguration.getFieldId(), subFieldConfiguration.getLength(), subFieldConfiguration.getDescription(), subFieldConfiguration.getCondId(), subFieldConfiguration.getPad());
                                isTlv = subFieldConfiguration.getIsTlvFormat();
                                j++;
                            }

                            if (isTlv) {
                                container[i] = new ISOMsgContainer(
                                        new TLVFieldBaseContainer(
                                                subContainer
                                        ),
                                        handlerMapper(fieldConfiguration.getEncodingId().getEncodingId().intValue(), fieldConfiguration.getFormatId().getFormatId(), fieldConfiguration.getFieldId(), fieldConfiguration.getLength(), fieldConfiguration.getDescription(), fieldConfiguration.getCondId(), fieldConfiguration.getPad())
                                );
                            } else if (subContainer.length > 0 && subContainer[0] instanceof A_BITMAP) {
                                container[i] = new ISOMsgContainer(
                                        new SubBitmapBaseContainer(
                                                subContainer
                                        ),
                                        handlerMapper(fieldConfiguration.getEncodingId().getEncodingId().intValue(), fieldConfiguration.getFormatId().getFormatId(), fieldConfiguration.getFieldId(), fieldConfiguration.getLength(), fieldConfiguration.getDescription(), fieldConfiguration.getCondId(), fieldConfiguration.getPad())
                                );
                            } else {
                                container[i] = new ISOMsgContainer(
                                        new SubFieldBaseContainer(
                                                subContainer
                                        ),
                                        handlerMapper(fieldConfiguration.getEncodingId().getEncodingId().intValue(), fieldConfiguration.getFormatId().getFormatId(), fieldConfiguration.getFieldId(), fieldConfiguration.getLength(), fieldConfiguration.getDescription(), fieldConfiguration.getCondId(), fieldConfiguration.getPad())
                                );
                            } //TODO: Need to refactor this EMV Field Container Handler to specific requirement later
                        } else if (fieldConfiguration.getFieldId() == 55) {
                            container[i] = new ISOMsgContainer(
                                    new EMVFieldBaseContainer(
                                            emvConfigurations,
                                            new EMVDataHandler()
                                    ),
                                    handlerMapper(fieldConfiguration.getEncodingId().getEncodingId().intValue(), fieldConfiguration.getFormatId().getFormatId(), fieldConfiguration.getFieldId(), fieldConfiguration.getLength(), fieldConfiguration.getDescription(), fieldConfiguration.getCondId(), fieldConfiguration.getPad())
                            );
                        } else {
                            container[i] = handlerMapper(fieldConfiguration.getEncodingId().getEncodingId().intValue(), fieldConfiguration.getFormatId().getFormatId(), fieldConfiguration.getFieldId(), fieldConfiguration.getLength(), fieldConfiguration.getDescription(), fieldConfiguration.getCondId(), fieldConfiguration.getPad());
                        }
                    }

                    LOGGER.debug(
                            String.format(
                                    "Success setting iso container with network id: %s with total configuration: %d",
                                    key, container.length
                            )
                    );

                } catch (Exception e) {
                    LOGGER.error(
                            String.format(
                                    "Error during setting iso container with network id: %s. Detail: %s",
                                    key, e.getMessage()
                            )
                    );
                }
                fixedData.put(key, container);
            });

            if (fixedData.size() > 0) {
                LOGGER.info("Success setting iso container");
            } else {
                LOGGER.warn("[THERE IS NO DATA FOR ISO CONFIGURATION CONTAINER]");

            }

            return fixedData;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            e.printStackTrace();
            LOGGER.warn("[ERROR WHEN SETTING ISO CONFIGURATION CONTAINER]");
            return new LinkedHashMap<>();
        } finally {
            LOGGER.info("[SETTING ISO CONFIGURATION CONTAINER FINISHED]");
        }
    }
}
