package hbm.fraudDetectionSystem.RuleEngine.Core.Core.Component;


import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListValue.FraudValueRepository;
import hbm.fraudDetectionSystem.GeneralComponent.Utility.ApplicationContext;
import hbm.fraudDetectionSystem.RuleEngine.Core.Util.DSLResolver;
import hbm.fraudDetectionSystem.RuleEngine.Core.Util.MVELParser;
import hbm.fraudDetectionSystem.RuleEngine.Core.Util.TypeChecker;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ListEngine extends DSLResolver {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final Map<String, String> transactionData;
    protected final FraudValueRepository valueRepository;
    protected boolean evaluateStatus = false;

    public ListEngine(Map<String, String> transactionData)   {
        this.transactionData = transactionData;
        this.valueRepository = ApplicationContext.getBean("fraudValueRepository", FraudValueRepository.class);
    }

    public void run(String expression) throws Exception {
        LOGGER.info(
                String.format(
                        "Initial Expression: %s",
                        expression
                )
        );

        //This method will run the magic bro
        String resolvedCondition = resolveConditionFormula(expression, this.transactionData, true);

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("input", this.transactionData);
        dataModel.put("tyChe", new TypeChecker());

        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(resolvedCondition);

        if (matcher.find()) {
            String fraudList = matcher.group(1);
            LOGGER.info(
                    String.format(
                            "Find all fraud value from fraud list: [%s]",
                            fraudList
                    )
            );

            List<String> fraudValues = this.valueRepository.findFraudValueByListName(fraudList);
            LOGGER.info(
                    String.format(
                            "Found fraud value with total data: [%s]",
                            fraudValues.size()
                    )
            );

            dataModel.put("fraudList", fraudValues);

            //Remove fraud list name from expression
            resolvedCondition = resolvedCondition.replaceAll("\\((" + fraudList + ")\\)", "");
        } else
            throw new IllegalArgumentException("List expression not valid, will stop processing this condition.");

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
