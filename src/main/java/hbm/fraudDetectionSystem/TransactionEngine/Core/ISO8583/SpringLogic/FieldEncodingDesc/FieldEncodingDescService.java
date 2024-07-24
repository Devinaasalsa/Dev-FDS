package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldEncodingDesc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional(rollbackOn = Exception.class)
public class FieldEncodingDescService {
    private final FieldEncodingDescRepository fieldEncodingDescRepository;

    @Autowired
    public FieldEncodingDescService(FieldEncodingDescRepository fieldEncodingDescRepository) {
        this.fieldEncodingDescRepository = fieldEncodingDescRepository;
    }

    public List<FieldEncodingDesc> findAllData() {
        return fieldEncodingDescRepository.findAllByOrderByEncodingIdAsc();
    }
}
