package hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelEndpointRepository extends JpaRepository<ChannelEndpoint, Long> {
    List<ChannelEndpoint> findAllByConfigIdOrderByUrlAsc(long configId);

    ChannelEndpoint findByEndpointId(long endpointId);

    Optional<ChannelEndpoint> findByUrlAndConfigId(String url, long configId);
}
