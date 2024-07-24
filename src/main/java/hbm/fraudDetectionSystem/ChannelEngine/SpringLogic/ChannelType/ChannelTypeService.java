package hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ChannelTypeService {
    protected final ChannelTypeRepository channelTypeRepository;

    @Autowired
    public ChannelTypeService(ChannelTypeRepository channelTypeRepository) {
        this.channelTypeRepository = channelTypeRepository;
    }

    public List<ChannelType> fetchAllChannelType() {
        return channelTypeRepository.findAll();
    }
}
