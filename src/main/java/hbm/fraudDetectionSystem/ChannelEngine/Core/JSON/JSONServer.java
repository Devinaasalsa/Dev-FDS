package hbm.fraudDetectionSystem.ChannelEngine.Core.JSON;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hbm.fraudDetectionSystem.ChannelEngine.Exception.JSONChannelException;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration.ChannelConfiguration;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration.ChannelConfigurationService;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint.ChannelEndpoint;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint.ChannelEndpointRepository;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Component.JSONBaseContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.JSONAuthListener;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.JSONRequestListener;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldConfiguration.JSONFieldConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONHeaderConfiguration.JSONHeaderConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransDataAttribute.TransDataAttribute;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class JSONServer {
    /*
        String = Base endpoint
        String = url
        Integer = State (Req / Resp),
        String = Field Name
    */
    protected final Map<String, Map<String, Map<Integer, List<JSONFieldConfiguration>>>> jsonFieldConfigurations;
    protected final Map<String, Map<String, Map<Integer, List<JSONHeaderConfiguration>>>> jsonHeaderConfigurations;
    /*
        String = ConfigId
        String = Url
     */
    protected final Map<String, Map<String, List<TransDataAttribute>>> jsonTransDataAttributes;
    protected final ChannelConfigurationService channelConfigurationService;
    protected final ChannelEndpointRepository channelEndpointRepository;
    protected final JSONAuthListener authListener;
    protected final JSONRequestListener requestListener;
    private Logger LOGGER = LoggerFactory.getLogger(getClass());


    @Autowired
    public JSONServer(Map<String, Map<String, Map<Integer, List<JSONFieldConfiguration>>>> jsonFieldConfigurations, Map<String, Map<String, Map<Integer, List<JSONHeaderConfiguration>>>> jsonHeaderConfigurations, @Qualifier("jsonTransDataAttributes") Map<String, Map<String, List<TransDataAttribute>>> jsonTransDataAttributes, ChannelConfigurationService channelConfigurationService, ChannelEndpointRepository channelEndpointRepository, JSONAuthListener authListener, JSONRequestListener requestListener) {
        this.jsonFieldConfigurations = jsonFieldConfigurations;
        this.jsonHeaderConfigurations = jsonHeaderConfigurations;
        this.jsonTransDataAttributes = jsonTransDataAttributes;
        this.channelConfigurationService = channelConfigurationService;
        this.channelEndpointRepository = channelEndpointRepository;
        this.authListener = authListener;
        this.requestListener = requestListener;
    }

    public ObjectNode run(HttpServletRequest servletRequest, String baseEndpoint, String url, JsonNode headerNode, JsonNode requestNode) throws JsonProcessingException {
        String clientIp = servletRequest.getHeader("host");
        String userAgent = servletRequest.getHeader("user-agent");
        ObjectMapper om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);
        ObjectNode responseNode;

        LOGGER.info(
                String.format(
                        "Incoming request, detail client: \nIp: %s \nUser Agent: %s \nBase Endpoint: %s \nUrl: %s \nHeader: %s \nPayload: %s",
                        clientIp, userAgent, baseEndpoint, url, om.writeValueAsString(headerNode), om.writeValueAsString(requestNode)
                )
        );

        ChannelConfiguration cc = this.getChannelConfiguration(baseEndpoint);
        this.validateBaseEndpoint(cc);
        this.validateUrl(cc, url);

        JSONBaseContainer reqContainer = this.createBaseContainer(cc.getMsgConfig().getConfigId() + "|" + baseEndpoint, url, 1);
        JSONBaseContainer respContainer = this.createBaseContainer(cc.getMsgConfig().getConfigId() + "|" + baseEndpoint, url, 2);
        List<TransDataAttribute> dataAttributes = this.getDataAttributes(cc, url);

        LOGGER.info("Checking Request type");

        if (this.isAuthUrl(cc.getEndpoint())){
            LOGGER.info("Request Type: [AUTH]");
            LOGGER.info("Send data to [Auth Engine]");
            responseNode = this.processRequestAuth(cc, headerNode, requestNode, reqContainer, respContainer, dataAttributes);

        } else {
            LOGGER.info("Request Type: [TRANSACTION]");
            LOGGER.info("Send data to [Transaction Engine]");
            responseNode = this.processRequestNode(cc, headerNode, requestNode, reqContainer, respContainer, dataAttributes);
        }

        LOGGER.info("Receive data from [Transaction Engine]");
        LOGGER.info(
                String.format(
                        "Response body: \n%s",
                        om.writeValueAsString(responseNode)
                )
        );
        LOGGER.info("Sending response to client");

        return responseNode;
    }

    protected boolean isAuthUrl(ChannelEndpoint ce) {
        return ce.getIsAuth();
    }

    protected ChannelConfiguration getChannelConfiguration(String baseEndpoint) {
        return this.channelConfigurationService.fetchChannelConfigByBaseEndpoint(baseEndpoint);
    }

    protected void validateBaseEndpoint(ChannelConfiguration cc) {
        if (cc == null) {
            throw new JSONChannelException("Base endpoint isn't listed");
        }
    }

    protected void validateUrl(ChannelConfiguration cc, String url) {
        ChannelEndpoint urlListed = cc.getMsgConfig().getEndpoints()
                .stream()
                .filter(v1 -> v1.getUrl().equals(url))
                .findFirst()
                .orElseThrow(() -> new JSONChannelException("Endpoint url isn't listed"));

        cc.setEndpoint(urlListed);
    }

    protected JSONBaseContainer createBaseContainer(String baseEndpoint, String url, int state) {
        JSONBaseContainer container = new JSONBaseContainer(
                this.getHeaderFieldConfiguration(baseEndpoint, url),
                this.getFieldConfiguration(baseEndpoint, url, state)
        );
        return container;
    }

    protected List<JSONHeaderConfiguration> getHeaderFieldConfiguration(String baseEndpoint, String url) {
        /*
            Uncomment this if later we need to have validation on header field
         */
        Map<String, Map<Integer, List<JSONHeaderConfiguration>>> filteredConfigByBaseEndpoint = this.filterHeaderConfigByBaseEndpoint(baseEndpoint);
        Map<Integer, List<JSONHeaderConfiguration>> filteredConfigByUrl = this.filterHeaderConfigByUrl(filteredConfigByBaseEndpoint, url);
        return this.filterHeaderConfigByState(filteredConfigByUrl, 1);

//        return new LinkedList<>();
    }

    protected Map<String, Map<Integer, List<JSONHeaderConfiguration>>> filterHeaderConfigByBaseEndpoint(String baseEndpoint) {
        Map<String, Map<Integer, List<JSONHeaderConfiguration>>> filteredConfigByBaseEndpoint = this.jsonHeaderConfigurations.get(baseEndpoint);
        if (filteredConfigByBaseEndpoint == null) {
            throw new RuntimeException("Configuration isn't found");
        }
        return filteredConfigByBaseEndpoint;
    }

    protected Map<Integer, List<JSONHeaderConfiguration>> filterHeaderConfigByUrl(Map<String, Map<Integer, List<JSONHeaderConfiguration>>> v1, String url) {
        Map<Integer, List<JSONHeaderConfiguration>> filteredConfigByUrl = v1.get(url);
        if (filteredConfigByUrl == null) {
            return new LinkedHashMap<>();
        }
        return filteredConfigByUrl;
    }

    protected List<JSONHeaderConfiguration> filterHeaderConfigByState(Map<Integer, List<JSONHeaderConfiguration>> v1, int state) {
        List<JSONHeaderConfiguration> filteredConfigByState = v1.get(state);
        if (filteredConfigByState == null) {
            return new LinkedList<>();
        }
        return filteredConfigByState;
    }

    protected List<JSONFieldConfiguration> getFieldConfiguration(String baseEndpoint, String url, int state) {
        Map<String, Map<Integer, List<JSONFieldConfiguration>>> filteredConfigByBaseEndpoint = this.filterFieldConfigByBaseEndpoint(baseEndpoint);
        Map<Integer, List<JSONFieldConfiguration>> filteredConfigByUrl = this.filterFieldConfigByUrl(filteredConfigByBaseEndpoint, url);
        return this.filterFieldConfigByState(filteredConfigByUrl, state);
    }

    protected Map<String, Map<Integer, List<JSONFieldConfiguration>>> filterFieldConfigByBaseEndpoint(String baseEndpoint) {
        Map<String, Map<Integer, List<JSONFieldConfiguration>>> filteredConfigByBaseEndpoint = this.jsonFieldConfigurations.get(baseEndpoint);
        if (filteredConfigByBaseEndpoint == null) {
            throw new RuntimeException("Configuration isn't found");
        }
        return filteredConfigByBaseEndpoint;
    }

    protected Map<Integer, List<JSONFieldConfiguration>> filterFieldConfigByUrl(Map<String, Map<Integer, List<JSONFieldConfiguration>>> v1, String url) {
        Map<Integer, List<JSONFieldConfiguration>> filteredConfigByUrl = v1.get(url);
        if (filteredConfigByUrl == null) {
            throw new RuntimeException("Configuration isn't found");
        }
        return filteredConfigByUrl;
    }

    protected List<JSONFieldConfiguration> filterFieldConfigByState(Map<Integer, List<JSONFieldConfiguration>> v1, int state) {
        List<JSONFieldConfiguration> filteredConfigByState = v1.get(state);
        if (filteredConfigByState == null) {
            throw new RuntimeException("Configuration isn't found");
        }
        return filteredConfigByState;
    }

    protected List<TransDataAttribute> getDataAttributes(ChannelConfiguration cc, String url) {
        Map<String, List<TransDataAttribute>> filteredByConfigId = this.filterDataAttrByConfigId(cc.getMsgConfig().getConfigId().toString());
        List<TransDataAttribute> dataAttributes = this.filterDataAttrByUrl(filteredByConfigId, url);
        if (dataAttributes == null) {
            throw new RuntimeException("Data attributes isn't found");
        }
        return dataAttributes;
    }

    protected Map<String, List<TransDataAttribute>> filterDataAttrByConfigId(String configId) {
        Map<String, List<TransDataAttribute>> fetchData = this.jsonTransDataAttributes.get(configId);
        if (fetchData == null) {
            throw new RuntimeException("Data attributes isn't found");
        }
        return fetchData;
    }

    protected List<TransDataAttribute> filterDataAttrByUrl(Map<String, List<TransDataAttribute>> v1, String url) {
        List<TransDataAttribute> fetchData = v1.get(url);
        if (fetchData == null) {
            throw new RuntimeException("Data attributes isn't found");
        }
        return fetchData;
    }

    protected ObjectNode processRequestAuth(ChannelConfiguration cc, JsonNode rawHeaderNode, JsonNode rawNode, JSONBaseContainer reqContainer, JSONBaseContainer respContainer, List<TransDataAttribute> dataAttributes) throws JsonProcessingException {
        return this.authListener.process(cc, rawHeaderNode, rawNode, reqContainer, respContainer, dataAttributes);
    }

    protected ObjectNode processRequestNode(ChannelConfiguration cc, JsonNode rawHeaderNode, JsonNode rawNode, JSONBaseContainer reqContainer, JSONBaseContainer respContainer, List<TransDataAttribute> dataAttributes) throws JsonProcessingException {
        return this.requestListener.process(cc, rawHeaderNode, rawNode, reqContainer, respContainer, dataAttributes);
    }
}
