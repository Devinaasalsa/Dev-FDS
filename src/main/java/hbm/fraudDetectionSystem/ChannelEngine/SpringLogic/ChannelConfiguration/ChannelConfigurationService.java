package hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ChannelConfigurationService {
    protected final ChannelConfigurationRepository channelConfigurationRepository;

    @Autowired
    public ChannelConfigurationService(ChannelConfigurationRepository channelConfigurationRepository) {
        this.channelConfigurationRepository = channelConfigurationRepository;
    }

    public List<ChannelConfiguration> fetchAllChannelConfig() {
        return channelConfigurationRepository.findAll();
    }

    public ChannelConfiguration fetchChannelConfigByBaseEndpoint(String value) {
        return this.channelConfigurationRepository.findByConnectionConfig_BaseEndpoint(value);
    }

    public void updateStatusByChannelId(Long id, int stat) {
        channelConfigurationRepository.updateStatusByChannelId(id, stat);
    }

    public String[] fetchAllBaseEndpoint() {
        return channelConfigurationRepository.findAllBaseEndpoint();
    }
}
