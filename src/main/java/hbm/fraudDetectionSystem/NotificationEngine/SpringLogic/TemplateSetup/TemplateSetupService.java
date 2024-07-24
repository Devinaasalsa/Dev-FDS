package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.TemplateSetup;

import hbm.fraudDetectionSystem.NotificationEngine.exception.NotificationTypeNotFoundException;

import java.util.List;
import java.util.Map;

public interface TemplateSetupService {
    List<TemplateSetup> findAllTemplateSetup();

    TemplateSetup addTemplateSetup(String templateText, String description, String subjectText, long notificationType) throws NotificationTypeNotFoundException;

    TemplateSetup updateTemplateSetup(long currentId, String newTemplateText, String newDescription, String subjectText, long newNotificationType) throws NotificationTypeNotFoundException;

    void deleteTemplateId(long templateId);

    TemplateSetup findByTemplateId(long templateId);

    List<TemplateSetup> search(Map<String, Object> reqBody);
}
