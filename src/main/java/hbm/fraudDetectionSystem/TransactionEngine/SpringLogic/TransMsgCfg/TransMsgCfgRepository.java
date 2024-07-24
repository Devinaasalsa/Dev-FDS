package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransMsgCfg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransMsgCfgRepository extends JpaRepository<TransMsgCfg, Long> {
    List<TransMsgCfg> findAllByOrderByConfigIdAscTransTypeAscFldAsc();
}
