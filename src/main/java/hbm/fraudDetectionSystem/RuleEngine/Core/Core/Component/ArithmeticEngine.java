package hbm.fraudDetectionSystem.RuleEngine.Core.Core.Component;



import hbm.fraudDetectionSystem.RuleEngine.Core.Util.DSLResolver;
import hbm.fraudDetectionSystem.RuleEngine.Core.Util.MVELParser;
import hbm.fraudDetectionSystem.RuleEngine.Core.Util.TypeChecker;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ArithmeticEngine extends DSLResolver {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final Map<String, String> transactionData;
    protected boolean evaluateStatus = false;

    public ArithmeticEngine(Map<String, String> transactionData) {
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
//        String regexPattern = "\\b(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\b";

        String resolvedCondition = resolveConditionFormula(expression, this.transactionData, false);
//                .replaceAll(regexPattern, "'$1'")''

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("input", this.transactionData);
        dataModel.put("tyChe", new TypeChecker());
        dataModel.put("DurationBetween", java.time.Duration.class.getMethod("between", java.time.temporal.Temporal.class, java.time.temporal.Temporal.class));
        dataModel.put("parse", java.time.LocalDateTime.class.getMethod("parse", CharSequence.class, DateTimeFormatter.class));
        dataModel.put("pattern", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

        //This will evaluate the expression
        LOGGER.info(
                "Start evaluate expression..."
        );
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
