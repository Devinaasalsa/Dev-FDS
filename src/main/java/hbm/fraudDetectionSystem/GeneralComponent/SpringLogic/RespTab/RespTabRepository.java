package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.RespTab;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RespTabRepository extends JpaRepository<RespTab, Long> {
    Optional<RespTab> findByCode(String code);
}
