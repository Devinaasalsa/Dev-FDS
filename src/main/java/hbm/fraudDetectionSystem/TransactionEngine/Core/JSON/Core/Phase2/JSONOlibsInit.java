package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Phase2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration.ChannelConfiguration;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration.ChannelConfigurationRepository;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint.ChannelEndpoint;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ConnectionConfig.ConnectionConfig;
import hbm.fraudDetectionSystem.SecurityEngine.Utility.JWTTokenProvider;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Component.JSONBaseContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils.JSONHashHelper;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldConfiguration.JSONFieldConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransDataAttribute.TransDataAttribute;
import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class JSONOlibsInit {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Value("${olibs.username}")
    protected String olibsUsername;
    @Value("${olibs.password}")
    protected String olibsPassword;
    @Value("${olibs.timeout}")
    protected int olibsTimeout;
    @Value("${olibs.type}")
    protected String olibsType;
    @Value("${olibs.serviceCode}")
    protected String olibsServiceCode;
    @Value("${olibs.token}")
    protected String olibsToken;
    @Value("${olibs.timeCycle}")
    protected String olibsTimeCycle;
    protected String checksumRaw = olibsType + olibsServiceCode + olibsUsername;
    protected String authToken;
    protected final ObjectMapper om = new ObjectMapper();
    /*
        String = Base endpoint
        String = url
        Integer = State (Req / Resp),
        String = Field Name
    */
    protected final Map<String, Map<String, Map<Integer, List<JSONFieldConfiguration>>>> jsonFieldConfigurations;
    /*
        String = ConfigId
        String = Url
     */
    protected final Map<String, Map<String, List<TransDataAttribute>>> jsonTransDataAttributes;
    protected final JSONResponseListener responseListener;
    protected final JWTTokenProvider jwtTokenProvider;
    protected final ChannelConfigurationRepository channelConfigurationRepository;
    protected final TaskScheduler customTaskScheduler;

    @Autowired
    public JSONOlibsInit(ChannelConfigurationRepository channelConfigurationRepository, Map<String, Map<String, Map<Integer, List<JSONFieldConfiguration>>>> jsonFieldConfigurations, @Qualifier("jsonTransDataAttributes") Map<String, Map<String, List<TransDataAttribute>>> jsonTransDataAttributes, JSONResponseListener responseListener, JWTTokenProvider jwtTokenProvider, @Qualifier("customTaskScheduler") TaskScheduler customTaskScheduler) {
        this.channelConfigurationRepository = channelConfigurationRepository;
        this.jsonFieldConfigurations = jsonFieldConfigurations;
        this.jsonTransDataAttributes = jsonTransDataAttributes;
        this.responseListener = responseListener;
        this.jwtTokenProvider = jwtTokenProvider;
        this.customTaskScheduler = customTaskScheduler;
    }

    @PostConstruct
    public void task() {
        customTaskScheduler.schedule(this::inquiryOlibJobs, new CronTrigger(olibsTimeCycle));
        new Thread(this::inquiryOlibJobs).start();
    }

    public void inquiryOlibJobs() {
        UUID uniqueID = UUID.randomUUID();
        String uID = uniqueID.toString();
        MDC.put("req_id", uID);

        LOGGER.info("Start executing OLIBS Jobs");

        ChannelConfiguration cc = channelConfigurationRepository.findByMsgConfig_ConfigId(4);

        if (cc == null)
            return;

        ConnectionConfig connectionConfig = cc.getConnectionConfig();

        String baseUrl = String.format(
                "http://%s:%s/%s",
                connectionConfig.getRemoteAddr(), connectionConfig.getRemotePort(), connectionConfig.getBaseEndpoint()
        );

        try {
            LOGGER.info("Fetching auth token");

            CompletableFuture<ResponseEntity<ObjectNode>> responseFuture = new CompletableFuture<>();
            CompletableFuture<ResponseEntity<ObjectNode>> timeoutFuture = responseFuture
                    .orTimeout(olibsTimeout, TimeUnit.SECONDS);

            WebClient.builder()
                    .baseUrl(baseUrl)
//                    .filter(((request, next) -> next.exchange(request)
//                            .doOnNext(value -> {
//                                MDC.put("req_id", uID);
//                            }))
//                    )
                    .clientConnector(webClientConnector(uID))
                    .build()
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/login")
                            .build()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Token", olibsToken)
                    .bodyValue(
                            om.valueToTree(
                                    Map.of(
                                            "username", olibsUsername,
                                            "password", olibsPassword
                                    )
                            )
                    )
                    .retrieve()
                    .onStatus(HttpStatus::isError, clientResponse -> {
                        // Handle error responses here
                        LOGGER.info("Received error response: " + clientResponse.statusCode());
                        return Mono.error(new Exception("Received error response: " + clientResponse.statusCode()));
                    })
                    .toEntity(ObjectNode.class)
                    .subscribeOn(Schedulers.single())
                    .flatMap(response -> {
                        authToken = Objects.requireNonNull(response.getBody()).get("Authorization").asText();

                        LOGGER.info(
                                String.format(
                                        "Got auth token: %s",
                                        authToken
                                )
                        );

                        return Mono.just(response);
                    })
                    .toFuture()
                    .exceptionally(throwable -> {
                        LOGGER.info(throwable.getMessage());
                        return null;
                    })
                    .whenComplete((response, throwable) -> {
                        responseFuture.complete(response);
                    });
            timeoutFuture.join();

            for (ChannelEndpoint endpoint : cc.getMsgConfig().getEndpoints()) {
                apiSender(cc, connectionConfig, endpoint, authToken, baseUrl, uID);
            }
        } catch (CompletionException e) {
            LOGGER.info("Request timeout");
        } finally {
            MDC.remove("req_id");
        }

        MDC.remove("req_id");
    }

    protected void apiSender(ChannelConfiguration cc, ConnectionConfig connectionConfig, ChannelEndpoint endpoint, String authToken, String baseUrl, String uID) {
        try {
            LOGGER.info(
                    String.format(
                            "Start executing jobs: %s",
                            endpoint.getUrl()
                    )
            );

            CompletableFuture<ResponseEntity<ObjectNode>> responseFuture = new CompletableFuture<>();
            CompletableFuture<ResponseEntity<ObjectNode>> timeoutFuture = responseFuture
                    .orTimeout(olibsTimeout, TimeUnit.SECONDS);

            WebClient.builder()
                    .baseUrl(baseUrl)
                    .clientConnector(webClientConnector(uID))
                    .build()
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/" + endpoint.getUrl())
                            .build()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + authToken)
                    .bodyValue(
                            om.valueToTree(
                                    Map.of(
                                            "username", olibsUsername,
                                            "type", olibsType,
                                            "checksum", Objects.requireNonNull(JSONHashHelper.hashSHA256(checksumRaw))
                                    )
                            )
                    )
                    .retrieve()
                    .onStatus(HttpStatus::isError, clientResponse -> {
                        // Handle error responses here
                        LOGGER.info("Received error response: " + clientResponse.statusCode());
                        return Mono.error(new Exception("Received error response: " + clientResponse.statusCode()));
                    })
                    .toEntity(ObjectNode.class)
                    .subscribeOn(Schedulers.single())
                    .flatMap(response -> {
                        try {
                            LOGGER.info("Start processing response data from OLIBS");
                            JSONBaseContainer respContainer = this.createBaseContainer(cc.getMsgConfig().getConfigId() + "|" + connectionConfig.getBaseEndpoint(), endpoint.getUrl(), 2);
                            List<TransDataAttribute> dataAttributes = getDataAttributes(cc, endpoint.getUrl());
                            responseListener.process(cc, (ArrayNode) Objects.requireNonNull(response.getBody()).get("data"), respContainer, dataAttributes, endpoint);
                        } catch (Exception e) {
                            LOGGER.info(e.getMessage());
                        }

                        return Mono.just(response);
                    })
                    .toFuture()
                    .exceptionally(throwable -> {
                        LOGGER.info(throwable.getMessage());
                        return null;
                    })
                    .whenComplete((response, throwable) -> {
                        responseFuture.complete(response);
                    });
            timeoutFuture.join();
        } catch (CompletionException e) {
            LOGGER.info("Request timeout");
        }
    }

    protected ClientHttpConnector webClientConnector(String uID) {
        HttpClient httpClient = HttpClient.create()
                .wiretap(this.getClass().getCanonicalName(), LogLevel.INFO, AdvancedByteBufFormat.TEXTUAL)
                .doOnChannelInit((connectionObserver, channel, socketAddress) -> {
                    MDC.put("req_id", uID);
                });
        return new ReactorClientHttpConnector(httpClient);
    }

    protected JSONBaseContainer createBaseContainer(String baseEndpoint, String url, int state) {
        return new JSONBaseContainer(
                null,
                this.getFieldConfiguration(baseEndpoint, url, state)
        );
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
}
