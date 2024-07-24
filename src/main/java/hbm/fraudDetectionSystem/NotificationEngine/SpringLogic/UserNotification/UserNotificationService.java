package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.UserNotification;

import java.sql.Timestamp;
import java.util.List;

public interface UserNotificationService {

    List<UserNotification>listCaseNotification(long userId);
    UserNotification addNotification(String initiator, String notificationType, String messageType, String recipientMessage,Timestamp dateOccurance, String description);
}
