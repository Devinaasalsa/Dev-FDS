package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.SubFieldConfiguration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubFieldConfigurationRepository extends JpaRepository<SubFieldConfiguration, Long> {
    List<SubFieldConfiguration> findAllByOrderByIdAsc();

    List<SubFieldConfiguration> findAllByParentIdIdOrderByFieldIdAsc(long id);

    SubFieldConfiguration findById(long id);

    Optional<SubFieldConfiguration> findByFieldIdAndPriorityAndParentIdId(Integer fieldId, Integer priority, long id);
}
