package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.NotificationType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {

    NotificationType findAllById(long id);
    List<NotificationType> findByOrderByIdAsc();
}
