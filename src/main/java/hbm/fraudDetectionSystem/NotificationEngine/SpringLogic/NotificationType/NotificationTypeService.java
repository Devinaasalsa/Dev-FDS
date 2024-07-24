package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.NotificationType;

import java.util.List;

public interface NotificationTypeService {

    List<NotificationType> getNotificationType();
    NotificationType addNotificationType(String notificationType);
    NotificationType updateNotificationType(long currentId, String newNotificationType);
    void deleteNotification(Long id);
    NotificationType findNotificationTypeById(long id);
}
