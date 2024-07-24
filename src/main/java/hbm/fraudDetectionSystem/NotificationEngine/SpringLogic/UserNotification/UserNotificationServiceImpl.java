package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.UserNotification;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.User;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class UserNotificationServiceImpl implements UserNotificationService {
    @Autowired
    private UserNotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    @Override
    public List<UserNotification>listCaseNotification(long userId) {
        User user = userService.findById(userId);
        return notificationRepository.findByReicipientMessage(user.getUsername());
    }


    @Override
    public UserNotification addNotification(String initiator, String notificationType,String messageType, String recipientMessage, Timestamp dateOccurance, String description) {
        UserNotification notification = new UserNotification();
        notification.setInitiator(initiator);
        notification.setNotificationType(notificationType);
        notification.setMessageType(messageType);
        notification.setReicipientMessage(recipientMessage);
        notification.setDateOccurance(dateOccurance);
        notification.setDescription(description);
        return notificationRepository.save(notification);
    }

}
