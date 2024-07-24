package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.EmailNotification;

import hbm.fraudDetectionSystem.ApplicationParameters.SpringLogic.AppParameters.ApplicationParameters;
import hbm.fraudDetectionSystem.ApplicationParameters.SpringLogic.AppParameters.ApplicationParametersService;
import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.RecipientGroup.RecipientGroup;
import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.RecipientGroup.RecipientGroupService;
import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.RecipientSetup.RecipientSetup;
import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.RecipientSetup.RecipientSetupService;
import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.TemplateSetup.TemplateSetup;
import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.TemplateSetup.TemplateSetupService;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule.Rule;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static hbm.fraudDetectionSystem.NotificationEngine.constant.EmailConfigValueConstant.*;
import static hbm.fraudDetectionSystem.NotificationEngine.enumeration.RecipientTypeEnum.RECIPIENT;
import static hbm.fraudDetectionSystem.NotificationEngine.enumeration.RecipientTypeEnum.RECIPIENT_GROUP;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    private Logger LOGGER = LoggerFactory.getLogger("DEF-U");

    public static final Pattern COND_PATTERN = Pattern.compile("<([^>]*)>");

    @Autowired
    private ApplicationParametersService parametersService;

    @Autowired
    private RecipientSetupService recipientSetupService;

    @Autowired
    private RecipientGroupService groupService;

    @Autowired
    private TemplateSetupService templateSetupService;

    @Override
    public void sendEmail(String recipientType, Long recipientId, Long recipientGroupId, Long templateId, Map<String, String> transDetails, List<Rule> ruleTriggered) throws MessagingException {
        List<ApplicationParameters> parameters = parametersService.listAllApplicationParameters();
        HashMap<String, String> emailConfig = new HashMap<>();
        parameters.forEach(applicationParameters -> {
            emailConfig.put(applicationParameters.getParamName(), applicationParameters.getValue());
        });
        Properties props = validateEmailConfig(emailConfig);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailConfig.get(EMAIL_USERNAME), emailConfig.get(EMAIL_PASSWORD));
            }
        });

        try {
            Message message = validateBeforeSendingEmail(new MimeMessage(session), emailConfig.get(EMAIL_USERNAME),
                    recipientType, recipientId, recipientGroupId, templateId, transDetails, ruleTriggered);
            Transport.send(message);
            LOGGER.info("Email sent successfully to recipient!");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

    @Override
    public void sendDefaultPass(String recipient, String recipientName, String password) {
        List<ApplicationParameters> parameters = parametersService.listAllApplicationParameters();
        HashMap<String, String> emailConfig = new HashMap<>();
        parameters.forEach(applicationParameters -> {
            emailConfig.put(applicationParameters.getParamName(), applicationParameters.getValue());
        });
        Properties props = validateEmailConfig(emailConfig);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailConfig.get(EMAIL_USERNAME), emailConfig.get(EMAIL_PASSWORD));
            }
        });

        try {
            Message message = buildDefaultPassMessage(new MimeMessage(session), emailConfig.get(EMAIL_USERNAME),
                    recipient, recipientName, password);
            Transport.send(message);
            LOGGER.info("Email default password sent successfully to recipient!");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
    }

    private Message validateBeforeSendingEmail(MimeMessage message, String email_username, String recipientType, Long recipientId, Long recipientGroupId, Long templateId, Map<String, String> transDetails, List<Rule> ruleTriggered) throws MessagingException {
        TemplateSetup templateSetup = templateSetupService.findByTemplateId(templateId);
        if (recipientType.equals(RECIPIENT.getRecipientType())) {
            RecipientSetup recipient = recipientSetupService.findByRecipientId(recipientId);
            message.setFrom(new InternetAddress(email_username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient.getContactValue()));
            message.setSubject(templateSetup.getSubjectText());
            message.setText(validateTemplateText(templateSetup.getTemplateText(), transDetails, ruleTriggered));
            LOGGER.info("sending email to : " + recipient.getContactValue());
        }
        if (recipientType.equals(RECIPIENT_GROUP.getRecipientType())) {
            RecipientGroup recipientGroup = groupService.findByGroupId(recipientGroupId);
            List<RecipientSetup> recipients = recipientGroup.getRecipientSetups();
            InternetAddress[] recipientAddress = new InternetAddress[recipients.size()];
            for (int i = 0; i < recipients.size(); i++) {
                recipientAddress[i] = new InternetAddress(recipients.get(i).getContactValue());
                LOGGER.info("sending email to : " + recipients.get(i).getContactValue());
            }
            message.setFrom(new InternetAddress(email_username));
            message.setRecipients(Message.RecipientType.TO, recipientAddress);
            message.setSubject(templateSetup.getSubjectText());
            message.setText(validateTemplateText(templateSetup.getTemplateText(), transDetails, ruleTriggered));

        }
        return message;
    }

    private String validateTemplateText(String templateText, Map<String, String> transDetails, List<Rule> ruleTriggered) {
        Matcher matcher = COND_PATTERN.matcher(templateText);
        Map<String, String> replacement = new HashMap<>();
        while (matcher.find()) {
            if (matcher.group(1).equals("hpan")) {
                String maskedCardNumber = transDetails.get("hpan").replaceAll("(?<=\\d{4})\\d(?=\\d{4})", "*");
                replacement.put(matcher.group(), maskedCardNumber);
            }
            if (matcher.group(1).equals("terminalId")) {
                replacement.put(matcher.group(), transDetails.get("terminalId"));
            }
            if (matcher.group(1).equals("sysdate")) {
                replacement.put(matcher.group(), transDetails.get("sysdate"));
            }
            if (matcher.group(1).equals("currency")) {
                replacement.put(matcher.group(), transDetails.get("currency"));
            }
            if (matcher.group(1).equals("refnum")) {
                replacement.put(matcher.group(), transDetails.get("rrn"));
            }
            if (matcher.group(1).equals("transType")) {
                replacement.put(matcher.group(), transDetails.get("transType"));
            }
            if (matcher.group(1).equals("respCode")) {
                replacement.put(matcher.group(), transDetails.get("respCode"));
            }
            if (matcher.group(1).equals("merchantType")) {
                replacement.put(matcher.group(), transDetails.get("merchantType"));
            }
            if (matcher.group(1).equals("rule")) {
                for (Rule rule : ruleTriggered) {
                    replacement.put(matcher.group(), rule.getRuleName());
                }
            }
            if (matcher.group(1).equals("rule_description")) {
                StringBuilder sb = new StringBuilder();
                for (Rule rule : ruleTriggered) {
                    String rulesDescription = sb.append(rule.getRuleName()).append(" ").append("(").append(rule.getDescription()).append(")").toString();
                    replacement.put(matcher.group(), rulesDescription);
                }
            }
        }
        for (Map.Entry<String, String> entry : replacement.entrySet()) {
            templateText = templateText.replace(entry.getKey(), entry.getValue());
        }
        return templateText;
    }

    protected Message buildDefaultPassMessage(MimeMessage message, String username, String recipient, String recipientName, String password) throws MessagingException {
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject("Default generated password");
        message.setText(
                String.format(
                        "Hi %s, \n\nthis is the default generated password by system. \nUsername: [%s] \nPassword: [%s] \nPlease reset the password once you login to the application \n\n\nRegards, \nFraud Detection System",
                        recipientName, recipientName, password
                )
        );

        return message;
    }

    private Properties validateEmailConfig(HashMap<String, String> emailConfig) {
        Properties props = new Properties();
        if (emailConfig.get(TLS_ENABLED).equals(FALSE)) {
            props.put(SMTP_HOST, emailConfig.get(EMAIL_HOST));
            props.put(SMTP_PORT, emailConfig.get(EMAIL_PORT));
            props.put(SMTP_AUTH, TRUE);
        } else {
            props.put(SMTP_HOST, emailConfig.get(EMAIL_HOST));
            props.put(SMTP_PORT, emailConfig.get(EMAIL_PORT));
            props.put(SMTP_AUTH, TRUE);
            props.put(STARTTLS_ENABLE, TRUE);
//            props.put(SSL_ENABLED, TRUE);
//            props.put(SSL_PROTOCOLS, TLSV_1_2);
        }
        return props;
    }
}
