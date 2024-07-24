package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.RecipientGroup;

import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.RecipientSetup.RecipientSetup;
import hbm.fraudDetectionSystem.NotificationEngine.exception.NotificationTypeNotFoundException;
import hbm.fraudDetectionSystem.NotificationEngine.exception.RecipientSetupNotFoundException;

import java.util.List;
import java.util.Map;

public interface RecipientGroupService {
    List<RecipientGroup> findAllRecipientGroup();
    RecipientGroup addRecipientGroup(String groupName, long notificationType, List<RecipientSetup> recipientSetup) throws NotificationTypeNotFoundException, RecipientSetupNotFoundException;
    RecipientGroup updateRecipientGroup(long currentId, String newGroupName, long newNotificationType, List<RecipientSetup> newRecipientSetup) throws NotificationTypeNotFoundException, RecipientSetupNotFoundException;
    void deleteGroupId(long groupId);
    RecipientGroup findByGroupId(long groupId);
    List<RecipientGroup> search(Map<String, Object> reqBody);
}
