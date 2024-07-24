package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransTypeTab;

import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.RespTab.RespTab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransTypeTabRepository extends JpaRepository<TransTypeTab, Long> {
    Optional<TransTypeTab> findByCode(String code);
}
