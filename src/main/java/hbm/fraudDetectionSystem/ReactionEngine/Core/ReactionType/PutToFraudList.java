package hbm.fraudDetectionSystem.ReactionEngine.Core.ReactionType;

import com.google.gson.Gson;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListValue.FraudValueServiceImpl;
import hbm.fraudDetectionSystem.GeneralComponent.Utility.ApplicationContext;
import hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.FraudReaction.FraudReaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;

@Slf4j
public class PutToFraudList implements Runnable {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final String reqId;
    protected final FraudReaction reaction;
    protected final Map<String, String> transactionData;
    protected final FraudValueServiceImpl fraudValueService;

    public PutToFraudList(String reqId, FraudReaction reaction, Map<String, String> transactionData) {
        this.reqId = reqId;
        this.reaction = reaction;
        this.transactionData = transactionData;
        this.fraudValueService = ApplicationContext.getBean("fraudValueServiceImpl", FraudValueServiceImpl.class);
    }

    @Override
    public void run() {
        MDC.put("req_id", this.reqId);
        LOGGER.info(
                String.format(
                        "START PROCESSING REACTION: [%s] - [ATTR_FRAUD_LIST]",
                        reaction.getId()
                )
        );

        try {
            ValueDataObject valueDataObject = new Gson().fromJson(this.reaction.getActionValue(), ValueDataObject.class);
            String value = this.transactionData.get(valueDataObject.getAttribute());

            if (value == null) {
                LOGGER.warn("Warning - value are unknown will skip insert the value to the list");
                return;
            }

            LOGGER.info(
                    String.format(
                            "Detail: \n\tList id: [%s] \n\tAttribute: [%s] \n\tValue: [%s]",
                            valueDataObject.getListId(), valueDataObject.getAttribute(), value
                    )
            );

            this.fraudValueService.addValue(
                    value,
                    "SYSTEM",
                    valueDataObject.getListId()
            );

            LOGGER.info("Attribute put successfully");
            LOGGER.info(
                    String.format(
                            "FINISH PROCESSING REACTION: [%s] - [ATTR_FRAUD_LIST], STATUS: [SUCCESS]",
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
                            "FINISH PROCESSING REACTION: [%s] - [ATTR_FRAUD_LIST], STATUS: [ERROR]",
                            reaction.getId()
                    )
            );
        } finally {
            MDC.remove("req_id");
        }
    }

    @Getter
    @AllArgsConstructor
    private static class ValueDataObject {
        private long listId;
        private String attribute;
    }
}
