package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.EMVFieldConfiguration;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EMVFieldConfigurationRepository extends JpaRepository<EMVFieldConfiguration, Long> {
    List<EMVFieldConfiguration> findAllByOrderByIdAsc();
}
