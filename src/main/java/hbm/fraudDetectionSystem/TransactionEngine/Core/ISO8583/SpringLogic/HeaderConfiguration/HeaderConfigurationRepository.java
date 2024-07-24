package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.HeaderConfiguration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HeaderConfigurationRepository extends JpaRepository<HeaderConfiguration, Long> {
    List<HeaderConfiguration> findAllByOrderByConfigIdAscFieldIdAscPriorityAsc();

    List<HeaderConfiguration> findAllByConfigIdConfigIdOrderByConfigIdAscFieldIdAscPriorityAsc(long id);

    Optional<HeaderConfiguration> findByFieldIdAndPriorityAndConfigIdConfigId(Integer fieldId, Integer priority, Long configId);

    @Modifying
    @Query(value = HeaderConfigurationQuery.INSERT_ISO_HEADER_QUERY, nativeQuery = true)
    int saveData(
            @Param("headerField") int headerField,
            @Param("headerLength") int headerLength,
            @Param("description") String description,
            @Param("priority") int priority,
            @Param("headerFormat") Long headerFormat,
            @Param("encoding") Long encoding,
            @Param("configId") Long configId
    );
}
