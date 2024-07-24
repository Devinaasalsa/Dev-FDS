package hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint;

import hbm.fraudDetectionSystem.ChannelEngine.Domain.ChannelEndpointHelper;
import hbm.fraudDetectionSystem.ChannelEngine.Enum.EndpointType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ChannelEndpointService {
    protected final ChannelEndpointRepository channelEndpointRepository;

    @Autowired
    public ChannelEndpointService(ChannelEndpointRepository channelEndpointRepository) {
        this.channelEndpointRepository = channelEndpointRepository;
    }

    public List<ChannelEndpoint> fetchAll(long configId) {
        return this.channelEndpointRepository.findAllByConfigIdOrderByUrlAsc(configId);
    }

    public ChannelEndpointHelper findByEndpointId(long endpointId) {
        ChannelEndpoint fetchedCE = this.channelEndpointRepository.findByEndpointId(endpointId);

        if (fetchedCE.getIsAuth()) {
            return new ChannelEndpointHelper()
                    .builder()
                    .url(fetchedCE.getUrl())
                    .configId(fetchedCE.getConfigId())
                    .endpointId(fetchedCE.getEndpointId())
                    .endpointType(EndpointType.AUTH)
                    .build();
        }

        if (fetchedCE.getSysMti().equals("0200")) {
            return new ChannelEndpointHelper()
                    .builder()
                    .url(fetchedCE.getUrl())
                    .configId(fetchedCE.getConfigId())
                    .endpointId(fetchedCE.getEndpointId())
                    .endpointType(EndpointType.ORIGINAL)
                    .build();
        }

        if (fetchedCE.getSysMti().equals("0400")) {
            return new ChannelEndpointHelper()
                    .builder()
                    .url(fetchedCE.getUrl())
                    .configId(fetchedCE.getConfigId())
                    .endpointId(fetchedCE.getEndpointId())
                    .endpointType(EndpointType.REVERSAL)
                    .build();
        }

        throw new RuntimeException("Endpoint Type is unknown");
    }

    public void add(ChannelEndpointHelper reqBody) {
        this.validateDataAlreadyPresent(reqBody);
        this.dataMapper(reqBody);
    }

    public void update(ChannelEndpointHelper reqBody) {
        this.validateDataId(reqBody.getEndpointId());
        this.validateDataAlreadyPresent(reqBody);
        this.dataMapper(reqBody);
    }

    public void delete(long id) {
        this.channelEndpointRepository.deleteById(id);
    }

    protected void validateDataAlreadyPresent(ChannelEndpointHelper reqBody) {
        this.channelEndpointRepository.findByUrlAndConfigId(reqBody.getUrl(), reqBody.getConfigId())
                .ifPresent(v1 -> {
                    if (!Objects.equals(v1.getEndpointId(), reqBody.getEndpointId()))
                        throw new RuntimeException("Data already exist");
                });
    }

    protected void validateDataId(long id) {
        this.channelEndpointRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Existing data not found"));
    }

    protected void dataMapper(ChannelEndpointHelper reqBody) {
        switch (reqBody.getEndpointType()) {
            case AUTH:
                this.channelEndpointRepository.save(
                        new ChannelEndpoint()
                                .builder()
                                .endpointId(reqBody.getEndpointId())
                                .url(reqBody.getUrl())
                                .isAuth(true)
                                .build()
                );
                break;

            case ORIGINAL:
                this.channelEndpointRepository.save(
                        new ChannelEndpoint()
                                .builder()
                                .endpointId(reqBody.getEndpointId())
                                .url(reqBody.getUrl())
                                .configId(reqBody.getConfigId())
                                .sysMti("0200")
                                .isAuth(false)
                                .build()
                );
                break;

            case REVERSAL:
                this.channelEndpointRepository.save(
                        new ChannelEndpoint()
                                .builder()
                                .endpointId(reqBody.getEndpointId())
                                .url(reqBody.getUrl())
                                .configId(reqBody.getConfigId())
                                .sysMti("0400")
                                .isAuth(false)
                                .build()
                );
                break;
        }
    }
}
