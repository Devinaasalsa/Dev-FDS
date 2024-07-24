package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldConfiguration;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FieldConfigurationRepository extends JpaRepository<FieldConfiguration, Long> {
    List<FieldConfiguration> findAllByOrderByConfigIdAscFieldIdAscPriorityAsc();
    List<FieldConfiguration> findAllByConfigIdConfigIdOrderByConfigIdAscFieldIdAscPriorityAsc(long id);
    Optional<FieldConfiguration> findByFieldIdAndPriorityAndConfigIdConfigId(Integer fieldId, Integer priority, Long configId);
}
