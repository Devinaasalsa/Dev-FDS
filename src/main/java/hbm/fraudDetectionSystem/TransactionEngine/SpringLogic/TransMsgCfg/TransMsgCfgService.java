package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransMsgCfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TransMsgCfgService {
    protected final TransMsgCfgRepository transMsgCfgRepository;

    @Autowired
    public TransMsgCfgService(TransMsgCfgRepository transMsgCfgRepository) {
        this.transMsgCfgRepository = transMsgCfgRepository;
    }

    public List<TransMsgCfg> fetchAllConfig() {
        return transMsgCfgRepository.findAllByOrderByConfigIdAscTransTypeAscFldAsc();
    }
}
