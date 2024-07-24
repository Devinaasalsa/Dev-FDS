package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.EMVFieldConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional(rollbackOn = Exception.class)
public class EMVFieldConfigurationService {
    private final EMVFieldConfigurationRepository emvFieldConfigurationRepository;
    @Autowired
    public EMVFieldConfigurationService(EMVFieldConfigurationRepository emvFieldConfigurationRepository) {
        this.emvFieldConfigurationRepository = emvFieldConfigurationRepository;
    }
    public List<EMVFieldConfiguration> findAllData() {
        return emvFieldConfigurationRepository.findAllByOrderByIdAsc();
    }
}
