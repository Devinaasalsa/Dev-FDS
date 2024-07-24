package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrAddtTrans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CurrAddtTransService {
    protected final CurrAddtTransRepository currAddtTransRepository;

    @Autowired
    public CurrAddtTransService(CurrAddtTransRepository currAddtTransRepository) {
        this.currAddtTransRepository = currAddtTransRepository;
    }

    public List<CurrAddtTrans> fetchDataByUtrnno(long utrnno) {
        return currAddtTransRepository.findAllByUtrnnoUtrnnoOrderByAttr(utrnno);
    }

    public void saveAllOfAddtData(List<CurrAddtTrans> data) {
        currAddtTransRepository.saveAll(data);
    }
}
