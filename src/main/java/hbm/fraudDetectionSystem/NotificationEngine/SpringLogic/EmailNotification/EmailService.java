package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.EmailNotification;

import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule.Rule;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Map;

public interface EmailService {
    void sendEmail(String recipientType, Long recipientId, Long recipientGroupId, Long templateId, Map<String, String> transDetails, List<Rule> ruleTriggered) throws MessagingException;

    void sendDefaultPass(String recipient, String recipientName, String password);
}
