package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtRespCode;

import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtTransType.ExtTransType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExtRespCodeRepository extends JpaRepository<ExtRespCode, Long> {
    List<ExtRespCode> findAllByConfigIdConfigId(long configId);

    Optional<ExtRespCode> findByConfigIdConfigIdAndIntResp_IdAndRespCode(long configId, long intResp, String respCode);
}
