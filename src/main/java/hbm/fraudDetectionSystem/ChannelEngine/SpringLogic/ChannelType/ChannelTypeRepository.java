package hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelTypeRepository extends JpaRepository<ChannelType, Long> {
}
