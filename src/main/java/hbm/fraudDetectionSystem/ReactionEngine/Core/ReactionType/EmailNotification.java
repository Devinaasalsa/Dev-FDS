package hbm.fraudDetectionSystem.ReactionEngine.Core.ReactionType;

import com.google.gson.Gson;
import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.EmailNotification.EmailServiceImpl;
import hbm.fraudDetectionSystem.GeneralComponent.Utility.ApplicationContext;
import hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.FraudReaction.FraudReaction;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.LinkedList;
import java.util.Map;

@Slf4j
public class EmailNotification implements Runnable {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected final String reqId;
    protected final FraudReaction reaction;
    protected final Map<String, String> transactionData;
    protected final EmailServiceImpl emailService;

    public EmailNotification(String reqId, FraudReaction reaction, Map<String, String> transactionData) {
        this.reqId = reqId;
        this.reaction = reaction;
        this.transactionData = transactionData;
        this.emailService = ApplicationContext.getBean("emailServiceImpl", EmailServiceImpl.class);
    }

    @Override
    public void run() {
        MDC.put("req_id", this.reqId);
        LOGGER.info(
                String.format(
                        "START PROCESSING REACTION: [%s] - [EMAIL_NOTIFICATION]",
                        reaction.getId()
                )
        );

        try {
            EmailDataObject emailDataObject = new Gson().fromJson(this.reaction.getActionValue(), EmailDataObject.class);

            LOGGER.info(
                    String.format(
                            "Detail: \n\tRecipient type: [%s] \n\tRecipient: [%s] \n\tRecipient group: [%s] \n\tTemplate id: [%s]",
                            emailDataObject.getRecipientType(), emailDataObject.getRecipient(), emailDataObject.getRecipientGroup(), emailDataObject.getTemplateId()
                    )
            );

            //TODO: More analyze related to rule detail
            this.emailService.sendEmail(emailDataObject.getRecipientType(), emailDataObject.getRecipient(), emailDataObject.getRecipientGroup(), emailDataObject.getTemplateId(), transactionData, new LinkedList<>());

            LOGGER.info("Email notification successfully generated");
            LOGGER.info(
                    String.format(
                            "FINISH PROCESSING REACTION: [%s] - [EMAIL_NOTIFICATION], STATUS: [SUCCESS]",
                            reaction.getId()
                    )
            );
        } catch (Exception e) {
            LOGGER.error("", e);
            LOGGER.info(
                    String.format(
                            "Email notification generation error, detail: [%s]",
                            e.getMessage()
                    )
            );
            LOGGER.info(
                    String.format(
                            "FINISH PROCESSING REACTION: [%s] - [EMAIL_NOTIFICATION], STATUS: [ERROR]",
                            reaction.getId()
                    )
            );
        } finally {
            MDC.remove("req_id");
        }
    }

    @Getter
    private static class EmailDataObject {
        private String recipientType;
        private long recipient;
        private long recipientGroup;
        private long templateId;

        public EmailDataObject(String recipientType, long recipient, long recipientGroup, long templateId) {
            this.recipientType = recipientType;
            this.recipient = recipient;
            this.recipientGroup = recipientGroup;
            this.templateId = templateId;
        }
    }
}
