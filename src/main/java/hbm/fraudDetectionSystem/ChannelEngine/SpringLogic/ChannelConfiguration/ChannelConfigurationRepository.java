package hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelConfigurationRepository extends JpaRepository<ChannelConfiguration, Long> {
    @Query(
            nativeQuery = true,
            value = "select * from CHANNEL_CONFIGURATION cc join MESSAGE_CONFIGURATION mc on mc.MSG_TYPE = :msgType and cc.CONFIG_ID = mc.CONFIG_ID"
    )
    List<ChannelConfiguration> findAllByMsgType(long msgType);

    ChannelConfiguration findByConnectionConfig_BaseEndpoint(String value);

    @Modifying
    @Query(nativeQuery = true, value = "update channel_configuration set stat=:stat where channel_id=:id")
    void updateStatusByChannelId(@Param("id")Long id, @Param("stat") int stat);

    @Query(
            nativeQuery = true,
            value = "select base_endpoint from channel_connection where base_endpoint is not null"
    )
    String[] findAllBaseEndpoint();

    @Query(
            nativeQuery = true,
            value = "select count(*) from channel_connection where base_endpoint = :baseEndpoint"
    )
    int findDataByBaseEndpoint(@Param("baseEndpoint") String baseEndpoint);

    ChannelConfiguration findByMsgConfig_ConfigId(long id);
}
