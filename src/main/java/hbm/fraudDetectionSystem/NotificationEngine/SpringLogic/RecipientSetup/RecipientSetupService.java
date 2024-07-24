package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.RecipientSetup;

import hbm.fraudDetectionSystem.NotificationEngine.exception.NotificationTypeNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface RecipientSetupService {
    List<RecipientSetup> findAllRecipientSetup();
    RecipientSetup addRecipientSetup(RecipientSetup setup) throws NotificationTypeNotFoundException;
    RecipientSetup updateRecipientSetup(RecipientSetup setup) throws NotificationTypeNotFoundException;
    void deleteRecipientId(long recipientId);
    RecipientSetup findByRecipientId(long recipientId);
    List<RecipientSetup> search(Map<String, Object> reqBody);
    void addRecipientSetupToGroup(Map<String, Long> reqBody) throws NotificationTypeNotFoundException;
    void removeRecipientSetupFromGroup(long id) throws NotificationTypeNotFoundException;
    List<RecipientSetup> findAllByGroupId(long groupId);
    void importContact(MultipartFile file) throws IOException;
}
