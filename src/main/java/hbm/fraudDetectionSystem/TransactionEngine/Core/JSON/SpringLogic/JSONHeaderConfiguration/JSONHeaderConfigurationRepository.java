package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONHeaderConfiguration;

import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Enum.JSONFieldState;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldConfiguration.JSONFieldConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JSONHeaderConfigurationRepository extends JpaRepository<JSONHeaderConfiguration, Long> {
    @Query(
            nativeQuery = true,
            value = "select jfc.*\n" +
                    "from JSON_HEADER_CONFIG jfc\n" +
                    "         left join MESSAGE_CONFIGURATION mc on jfc.CONFIG_ID = mc.CONFIG_ID and mc.MSG_TYPE = :msgType where jfc.CONFIG_ID = :configId and jfc.ENDPOINT_ID = :endpointId order by jfc.CONFIG_ID, jfc.STATE, jfc.SEQUENCE"
    )
    List<JSONHeaderConfiguration> findHeaderConfigurationByMsgType(
            @Param("configId") long configId,
            @Param("endpointId") long endpointId,
            @Param("msgType") long msgType
    );

    List<JSONHeaderConfiguration> findAllByMsgConfigConfigIdAndEndpointEndpointIdAndStateOrderBySequenceAsc(long configId, long endpointId, JSONFieldState state);

    Optional<JSONHeaderConfiguration> findByFieldNameAndSequenceAndEndpointEndpointIdAndMsgConfigConfigId(String fieldName, int sequence, long endpointId, long configId);
}
