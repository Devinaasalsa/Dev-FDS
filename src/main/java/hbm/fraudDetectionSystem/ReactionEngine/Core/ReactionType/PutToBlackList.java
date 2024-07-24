package hbm.fraudDetectionSystem.ReactionEngine.Core.ReactionType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.SpringLogic.FraudBlackListServiceImpl;
import hbm.fraudDetectionSystem.GeneralComponent.Utility.ApplicationContext;
import hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.FraudReaction.FraudReaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.sql.Timestamp;
import java.util.Map;

@Slf4j
public class PutToBlackList implements Runnable {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final String reqId;
    protected final FraudReaction reaction;
    protected final Map<String, String> transactionData;
    protected final FraudBlackListServiceImpl fraudBlackListService;

    public PutToBlackList(String reqId, FraudReaction reaction, Map<String, String> transactionData) {
        this.reqId = reqId;
        this.reaction = reaction;
        this.transactionData = transactionData;
        this.fraudBlackListService = ApplicationContext.getBean("fraudBlackListServiceImpl", FraudBlackListServiceImpl.class);
    }

    @Override
    public void run() {
        MDC.put("req_id", this.reqId);
        LOGGER.info(
                String.format(
                        "START PROCESSING REACTION: [%s] - [ATTR_BLACK_LIST]",
                        reaction.getId()
                )
        );

        try {
            Gson gson = new GsonBuilder().setDateFormat("MM/DD/YYYY HH:mm:ss").create();
            BlacklistDataObject dataObject = gson.fromJson(this.reaction.getActionValue(), BlacklistDataObject.class);
            String value = this.transactionData.get(dataObject.getAttribute());

            if (value == null) {
                LOGGER.warn("Warning - value are unknown will skip insert the value to the list");
                return;
            }

            LOGGER.info(
                    String.format(
                            "Detail: \n\tEntity type: [%s] \n\tValue: [%s] \n\tDate in: [%s] \n\tDate out: [%s]",
                            dataObject.getAttribute(), value, dataObject.getDateIn(), dataObject.getDateOut()
                    )
            );

            this.fraudBlackListService.addBlackList(
                    dataObject.getAttribute(),
                    value,
                    dataObject.getUGroupId(),
                    dataObject.getDateIn(),
                    dataObject.getDateOut(),
                    dataObject.getInitiatorId(),
                    "Reaction put attr to black list"
            );
            LOGGER.info("Attribute put successfully");
            LOGGER.info(
                    String.format(
                            "FINISH PROCESSING REACTION: [%s] - [ATTR_BLACK_LIST], STATUS: [SUCCESS]",
                            reaction.getId()
                    )
            );
        } catch (Exception e) {
            LOGGER.error("", e);
            LOGGER.info(
                    String.format(
                            "Put attribute error, detail: [%s]",
                            e.getMessage()
                    )
            );
            LOGGER.info(
                    String.format(
                            "FINISH PROCESSING REACTION: [%s] - [ATTR_BLACK_LIST], STATUS: [ERROR]",
                            reaction.getId()
                    )
            );
        } finally {
            MDC.remove("req_id");
        }
    }

    @Getter
    @AllArgsConstructor
    private static class BlacklistDataObject {
        private Timestamp dateIn;
        private Timestamp dateOut;
        private String attribute;
        private long uGroupId;
        private long initiatorId;
    }
}
