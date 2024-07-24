package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.NotificationType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationTypeServiceImpl implements NotificationTypeService{

    private NotificationTypeRepository repository;

    @Autowired
    public NotificationTypeServiceImpl(NotificationTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<NotificationType> getNotificationType() {
        return repository.findByOrderByIdAsc();
    }

    @Override
    public NotificationType addNotificationType(String notificationType) {
        NotificationType notifType = new NotificationType();
        notifType.setNotificationType(notificationType);
        repository.save(notifType);
        return notifType;
    }

    @Override
    public NotificationType updateNotificationType(long currentId, String newNotificationType) {
        NotificationType currentNotificationType = findNotifTypeById(currentId);
        currentNotificationType.setNotificationType(newNotificationType);
        repository.save(currentNotificationType);
        return currentNotificationType;
    }

    @Override
    public void deleteNotification(Long id) {
        NotificationType type = repository.findAllById(id);
        repository.deleteById(type.getId());
    }

    @Override
    public NotificationType findNotificationTypeById(long id) {
        return repository.findAllById(id);
    }

    private NotificationType findNotifTypeById(long currentId) {
        return repository.findAllById(currentId);
    }
}
