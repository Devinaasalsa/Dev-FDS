package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfigurationQuery.INSERT_ISO_CONFIG_QUERY;

@Repository
public interface MessageConfigurationRepository extends JpaRepository<MessageConfiguration, Long> {
    List<MessageConfiguration> findAllByMsgType_MsgIdOrderByConfigIdAsc(long msgType);
    MessageConfiguration findAllByConfigId(Long dataId);
    @Modifying
    @Query(value = INSERT_ISO_CONFIG_QUERY, nativeQuery = true)
    void saveData(@Param("description") String description, @Param("hasHeader") boolean hasHeader, @Param("name") String name);
}
