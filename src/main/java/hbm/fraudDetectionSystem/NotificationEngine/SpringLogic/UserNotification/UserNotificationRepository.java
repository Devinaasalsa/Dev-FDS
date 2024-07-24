package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.UserNotification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    List<UserNotification>findByReicipientMessage(String username);
}
