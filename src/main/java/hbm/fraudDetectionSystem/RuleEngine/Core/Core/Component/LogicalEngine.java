package hbm.fraudDetectionSystem.RuleEngine.Core.Core.Component;


import hbm.fraudDetectionSystem.RuleEngine.Core.Util.DSLResolver;
import hbm.fraudDetectionSystem.RuleEngine.Core.Util.MVELParser;
import hbm.fraudDetectionSystem.RuleEngine.Core.Util.TypeChecker;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LogicalEngine extends DSLResolver {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final Map<String, String> transactionData;
    protected boolean evaluateStatus = false;

    public LogicalEngine(Map<String, String> transactionData) {
        this.transactionData = transactionData;
    }

    public void run(String expression) throws Exception {
        LOGGER.info(
                String.format(
                        "Initial Expression: %s",
                        expression
                )
        );

        //This method will run the magic bro
        String resolvedCondition = resolveConditionFormula(expression, this.transactionData, false);

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("input", this.transactionData);
        dataModel.put("tyChe", new TypeChecker());

        //This will evaluate the expression
        LOGGER.info("Start evaluate expression...");

        this.evaluateStatus = MVELParser.evaluateConditionFormula(resolvedCondition, dataModel);
        LOGGER.info(
                String.format(
                        "Evaluate Result: [%s]",
                        this.evaluateStatus
                )
        );
    }

    @Override
    protected Object resolveValue(String depthHistory, String historyAttribute, String transField, Map<String, String> inputData, boolean ignoreType) {
        return this.fetchTransFieldByHistory(depthHistory, historyAttribute, transField, inputData, ignoreType);
    }

    public boolean getEvaluateStatus() {
        return evaluateStatus;
    }
}
