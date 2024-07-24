package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class JSONFieldFormatterService {
    protected final JSONFieldFormatterRepository jsonFieldFormatterRepository;

    @Autowired
    public JSONFieldFormatterService(JSONFieldFormatterRepository jsonFieldFormatterRepository) {
        this.jsonFieldFormatterRepository = jsonFieldFormatterRepository;
    }

    public List<JSONFieldFormatter> findAll() {
        return this.jsonFieldFormatterRepository.findAllByOrderByFormatterIdAsc();
    }
}
