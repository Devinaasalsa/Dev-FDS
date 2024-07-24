package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldFormatDesc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional(rollbackOn = Exception.class)
public class FieldFormatDescService {
    private final FieldFormatDescRepository fieldFormatDescRepository;

    @Autowired
    public FieldFormatDescService(FieldFormatDescRepository fieldFormatDescRepository) {
        this.fieldFormatDescRepository = fieldFormatDescRepository;
    }

    public List<FieldFormatDesc> findAllData() {
        return fieldFormatDescRepository.findAllByOrderByFormatIdAsc();
    }
}
