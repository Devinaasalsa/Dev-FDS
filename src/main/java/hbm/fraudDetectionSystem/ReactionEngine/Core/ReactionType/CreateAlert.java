package hbm.fraudDetectionSystem.ReactionEngine.Core.ReactionType;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.SpringLogic.Case.CaseServiceImpl;
import hbm.fraudDetectionSystem.GeneralComponent.Utility.ApplicationContext;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.MappingInfoTransaction.FraudInfoTransServiceImpl;
import hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.FraudReaction.FraudReaction;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;

@Slf4j
public class CreateAlert implements Runnable {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final String reqId;
    protected final FraudReaction reaction;
    protected final Map<String, String> transactionData;
    protected final CaseServiceImpl caseService;
    protected final FraudInfoTransServiceImpl fraudInfoTransService;

    public CreateAlert(String reqId, FraudReaction reaction, Map<String, String> transactionData) {
        this.reqId = reqId;
        this.reaction = reaction;
        this.transactionData = transactionData;
        this.caseService = ApplicationContext.getBean("caseServiceImpl", CaseServiceImpl.class);
        this.fraudInfoTransService = ApplicationContext.getBean("fraudInfoTransServiceImpl", FraudInfoTransServiceImpl.class);
    }

    @Override
    public void run() {
        MDC.put("req_id", this.reqId);
        LOGGER.info(
                String.format(
                        "START PROCESSING REACTION: [%s] - [CREATE_ALERT]",
                        reaction.getId()
                )
        );

        try {
            String refnum = this.transactionData.get("rrn") != null ? this.transactionData.get("rrn").replaceAll(" ", "") : null;
            String utrnno = this.transactionData.get("utrnno");
            String custNumb = this.transactionData.get("customerId");
            String cardNumb = this.transactionData.get("hpan");
            String accountNumb = this.transactionData.get("account");

            this.transactionData.put("bindingId", this.reaction.getBindingId().toString());
            this.transactionData.put("bindingType", this.reaction.getBindingType());
            this.transactionData.put("zone", this.reaction.getZone());

            this.caseService.generateCase(transactionData);
            this.fraudInfoTransService.addTransInfoByEntity(refnum,utrnno,custNumb,cardNumb,accountNumb);
            LOGGER.info("Alert successfully generated");
            LOGGER.info(
                    String.format(
                            "FINISH PROCESSING REACTION: [%s] - [CREATE_ALERT], STATUS: [SUCCESS]",
                            reaction.getId()
                    )
            );
        } catch (Exception e) {
            LOGGER.error("", e);
            LOGGER.info(
                    String.format(
                            "Alert generation error, detail: [%s]",
                            e.getMessage()
                    )
            );
            LOGGER.info(
                    String.format(
                            "FINISH PROCESSING REACTION: [%s] - [CREATE_ALERT], STATUS: [ERROR]",
                            reaction.getId()
                    )
            );
        } finally {
            MDC.remove("req_id");
        }
    }
}
