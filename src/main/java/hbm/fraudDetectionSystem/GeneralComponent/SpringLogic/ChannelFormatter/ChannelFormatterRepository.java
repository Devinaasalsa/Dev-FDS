package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ChannelFormatter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelFormatterRepository extends JpaRepository<ChannelFormatter, Long> {
}
