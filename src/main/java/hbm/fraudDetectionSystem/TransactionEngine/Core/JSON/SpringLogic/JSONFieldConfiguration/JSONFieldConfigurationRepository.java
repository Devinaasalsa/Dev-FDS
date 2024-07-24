package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldConfiguration;

import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Enum.JSONFieldState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JSONFieldConfigurationRepository extends JpaRepository<JSONFieldConfiguration, Long> {

    @Query(
            nativeQuery = true,
            value = "select *\n" +
                    "from json_field_config\n" +
                    "where config_id = :configId\n" +
                    "  and endpoint_id = :endpointId\n" +
                    "  and parent_field is null\n" +
                    "order by sequence"
    )
    List<JSONFieldConfiguration> findAllByConfigIdAndEndpoint(String configId, String endpointId);

    @Query(
            nativeQuery = true,
            value = "select jfc.*\n" +
                    "from JSON_FIELD_CONFIG jfc\n" +
                    "         left join MESSAGE_CONFIGURATION mc on jfc.CONFIG_ID = mc.CONFIG_ID and mc.MSG_TYPE = :msgType where jfc.CONFIG_ID = :configId and jfc.ENDPOINT_ID = :endpointId and jfc.PARENT_FIELD is null order by jfc.CONFIG_ID, jfc.STATE, jfc.SEQUENCE"
    )
    List<JSONFieldConfiguration> findParentConfigurationByMsgType(
            @Param("configId") long configId,
            @Param("endpointId") long endpointId,
            @Param("msgType") long msgType
    );

    @Query(
            nativeQuery = true,
            value = "select jfc.*\n" +
                    "from JSON_FIELD_CONFIG jfc\n" +
                    "         left join MESSAGE_CONFIGURATION mc on jfc.CONFIG_ID = mc.CONFIG_ID and mc.MSG_TYPE = :msgType where jfc.CONFIG_ID = :configId and jfc.ENDPOINT_ID = :endpointId and jfc.PARENT_FIELD = :parentField order by jfc.CONFIG_ID, jfc.STATE, jfc.SEQUENCE"
    )
    List<JSONFieldConfiguration> findChildConfigurationByMsgType(
            @Param("configId") long configId,
            @Param("endpointId") long endpointId,
            @Param("msgType") long msgType,
            @Param("parentField") long parentField
    );

    @Query(
            nativeQuery = true,
            value = "SELECT COALESCE(MAX(level), 0)\n\n" +
                    "from json_field_config\n" +
                    "where endpoint_id = :endpointId\n" +
                    "  and config_id = :configId\n" +
                    "  and state = :state"
    )
    int getLowLevel(
            @Param("endpointId") long endpointId,
            @Param("configId") long configId,
            @Param("state") int state
    );

    List<JSONFieldConfiguration> findAllByMsgConfigConfigIdAndEndpointEndpointIdAndStateAndLevelOrderByParentFieldAscSequenceAsc(long configId, long endpointId, JSONFieldState state, int level);

    Optional<JSONFieldConfiguration> findByFieldNameAndLevelAndParentFieldIsNullAndSequenceAndEndpointEndpointIdAndMsgConfigConfigId(String fieldName, int level, int sequence, long endpointId, long configId);

    Optional<JSONFieldConfiguration> findByFieldNameAndLevelAndParentFieldAndSequenceAndEndpointEndpointIdAndMsgConfigConfigId(String fieldName, int level, long parentField, int sequence, long endpointId, long configId);
}
