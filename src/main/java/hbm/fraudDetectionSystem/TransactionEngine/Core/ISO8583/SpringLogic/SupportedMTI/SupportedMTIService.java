package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.SupportedMTI;

import hbm.fraudDetectionSystem.GeneralComponent.Exception.DataBlankException;
import hbm.fraudDetectionSystem.GeneralComponent.Exception.DataNotFoundWhenUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.SpringLogicHelper.convertNull2Blank;
import static hbm.fraudDetectionSystem.GeneralComponent.Utility.SpringLogicHelper.isDataNotBlank;

@Service
@Transactional(rollbackOn = Exception.class)
public class SupportedMTIService {
    private final SupportedMTIRepository supportedMTIRepository;

    @Autowired
    public SupportedMTIService(SupportedMTIRepository supportedMTIRepository) {
        this.supportedMTIRepository = supportedMTIRepository;
    }

    public List<SupportedMTI> findAllData() {
        return supportedMTIRepository.findAllByOrderByIdAsc();
    }

    public void saveData(SupportedMTI data) throws DataBlankException {
        if (isDataNotBlank(convertNull2Blank(data.getValue())))
            supportedMTIRepository.saveData(data.getValue(), data.getIsResponse(), data.getIsReversal());
        else
            throw new DataBlankException("value");
    }

    public void updateData(SupportedMTI data) throws DataNotFoundWhenUpdate, DataBlankException {
        if (isDataNotBlank(convertNull2Blank(data.getValue()))) {
            Optional<SupportedMTI> fetchedData = supportedMTIRepository.findById(data.getId());
            if (fetchedData.isPresent()) {
                supportedMTIRepository.save(data);
            } else throw new DataNotFoundWhenUpdate(data.getId());
        } else
            throw new DataBlankException("value");
    }

    public void deleteData(Long id) {
        supportedMTIRepository.deleteById(id);
    }
}
