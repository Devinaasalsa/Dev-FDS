package hbm.fraudDetectionSystem.TransactionEngine.Core.Fraud;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.SpringLogic.FraudBlackListService;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.SpringLogic.FraudWhiteListService;
import hbm.fraudDetectionSystem.RuleEngine.Core.Core.RuleEngine;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class FraudDetection {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final FraudWhiteListService whiteListService;
    protected final FraudBlackListService blackListService;
    protected final RuleEngine ruleEngineService;

    public FraudDetection(FraudWhiteListService whiteListService, FraudBlackListService blackListService, RuleEngine ruleEngineService) {
        this.whiteListService = whiteListService;
        this.blackListService = blackListService;
        this.ruleEngineService = ruleEngineService;
    }

    protected String checkTransactionInWhiteList(Map<String, String> preparedData) {
        return whiteListService.run(preparedData);
    }

    protected String checkTransactionInBlackList(Map<String, String> preparedData) {
        return blackListService.run(preparedData);
    }

    protected String checkTransactionInRule(Map<String ,String> preparedData) {
        return ruleEngineService.run(preparedData);
    }

    public String detectTransaction(Map<String, String> preparedData) {
        String resp;
        LOGGER.info("Receive data from [Transaction Engine]");

        LOGGER.info("[Check Transaction in White List]");

        resp = this.checkTransactionInWhiteList(preparedData);
        LOGGER.info(
                String.format(
                        "Got response %s, from White List",
                        resp
                )
        );

        LOGGER.info(
                "[Finish Checking Data in White List]"
        );

        if (isTransactionFraud(resp)) {
            LOGGER.info("[Check Transaction in Black List]");
            resp = this.checkTransactionInBlackList(preparedData);
            LOGGER.info(
                    String.format(
                            "Got response %s, from Black List",
                            resp
                    )
            );

            LOGGER.info(
                    "[Finish Checking Data in Black List]"
            );

        }

        if (isTransactionFraud(resp)) {
            LOGGER.info("[Check Transaction in Rule]");
            resp = this.checkTransactionInRule(preparedData);
            LOGGER.info(
                    String.format(
                            "Got response %s, from Rule",
                            resp
                    )
            );

            LOGGER.info(
                    "[Finish Checking Data in Rule]"
            );

        }

        LOGGER.info("Send data to [Transaction Engine]");

        return resp;
    }

    protected boolean isTransactionFraud(String resp) {
        return resp == null;
    }
}
