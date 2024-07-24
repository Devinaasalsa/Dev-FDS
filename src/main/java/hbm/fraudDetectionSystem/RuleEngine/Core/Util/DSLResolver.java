package hbm.fraudDetectionSystem.RuleEngine.Core.Util;


import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans.CurrTransServiceImpl;
import hbm.fraudDetectionSystem.GeneralComponent.Utility.ApplicationContext;
import hbm.fraudDetectionSystem.RuleEngine.Enum.DSLKeywordType;
import hbm.fraudDetectionSystem.RuleEngine.Enum.HistoryAttributeEnum;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static hbm.fraudDetectionSystem.RuleEngine.Enum.HistoryAttributeEnum.*;

@Slf4j
public abstract class DSLResolver {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final CurrTransServiceImpl currTransService;

    protected DSLResolver() {
        this.currTransService = ApplicationContext.getBean("currTransServiceImpl", CurrTransServiceImpl.class);
    }

    protected String resolveConditionFormula(String expression, Map<String, String> transactionData, boolean ignoreType) throws Exception {
        return executeDSLResolver(expression, transactionData, ignoreType);
    }

    protected String executeDSLResolver(String expression, Map<String, String> transactionData, boolean ignoreType) throws Exception {
        /*
            Get list keyword, the type of variable is list because some rule
            have multiple keyword in 1 expression, ex. LOGICAL.
         */
        LOGGER.info("Starting DSL resolution with expression: " + expression);

        List<String> listKeyword = DSLPatternUtil.getListKeyword(expression);

        for (String dslKeyword : listKeyword) {
            //Extract keyword, actually only clear/remove the bracket "()"
            String extractedDslKeyword = DSLPatternUtil.extractKeyword(dslKeyword);
            LOGGER.info("Extracted Keyword: [" + extractedDslKeyword + "]");

            //Parsed the keyword and get each of attribute
            String conditionType = DSLPatternUtil.getKeywordValue(extractedDslKeyword, DSLKeywordType.CONDITION_TYPE);
            String depthHistory = DSLPatternUtil.getKeywordValue(extractedDslKeyword, DSLKeywordType.DEPTH_HISTORY);
            String historyAttr = DSLPatternUtil.getKeywordValue(extractedDslKeyword, DSLKeywordType.HISTORY_ATTR);
            String transField = DSLPatternUtil.getKeywordValue(extractedDslKeyword, DSLKeywordType.TRANS_FIELD);

            LOGGER.info(
                    String.format(
                            "Parsed keyword: \n\tCondition Type: [%s] \n\tDepth History: [%s] \n\tHistory By: [%s] \n\tField: [%s]",
                            conditionType, depthHistory, historyAttr, transField
                    )
            );

            /*
                Resolve the dynamic field to value
                each of keyword type have different process
                    - Constant
             */
            Object resolveValue = resolveValue(depthHistory, historyAttr, transField, transactionData, ignoreType);
            LOGGER.info("Resolve Value: [" + resolveValue + "]");

            expression = expression.replace(dslKeyword, resolveValue.toString());
        }

        LOGGER.info(
                String.format(
                        "Final expression result: [%s]",
                        expression
                )
        );

        return expression;
    }

    protected Object fetchTransFieldByHistory(String depthHistory, String historyAttribute, String transField, Map<String, String> inputData, boolean ignoreType) {
        if (depthHistory.equals("0")) {
            if (ignoreType)
                return new StringBuilder()
                        .append("tyChe.str(")
                        .append("input.").append(transField)
                        .append(")").toString();

            return new StringBuilder()
                    .append("tyChe.cvt(")
                    .append("input.").append(transField)
                    .append(")").toString();

        } else {
            String fetchValue;
            switch (HistoryAttributeEnum.fromName(historyAttribute)) {
                case CARD:
                    LOGGER.info(
                            String.format(
                                    "Find Trans Field By History, with data: \n\tHistory by: [%s] \n\tHistory value: [%s] \n\tDepth: [%s] \n\tTransaction field: [%s]",
                                    CARD.getName(), inputData.get("hpan"), Integer.parseInt(depthHistory), transField
                            )
                    );
                    fetchValue = this.currTransService.findTransFieldByHistory(CARD.getName(), inputData.get("hpan"), Integer.parseInt(depthHistory), transField);
                    break;

                case TERMINALID:
                    LOGGER.info(
                            String.format(
                                    "Find Trans Field By History, with data: \n\tHistory by: [%s] \n\tHistory value: [%s] \n\tDepth: [%s] \n\tTransaction field: [%s]",
                                    TERMINALID.getName(), inputData.get("terminalId"), Integer.parseInt(depthHistory), transField
                            )
                    );
                    fetchValue = this.currTransService.findTransFieldByHistory(TERMINALID.getName(), inputData.get("terminalId"), Integer.parseInt(depthHistory), transField);
                    break;

                case MERCHANTID:
                    LOGGER.info(
                            String.format(
                                    "Find Trans Field By History, with data: \n\tHistory by: [%s] \n\tHistory value: [%s] \n\tDepth: [%s] \n\tTransaction field: [%s]",
                                    MERCHANTID.getName(), inputData.get("merchantType"), Integer.parseInt(depthHistory), transField
                            )
                    );
                    fetchValue = this.currTransService.findTransFieldByHistory(MERCHANTID.getName(), inputData.get("merchantType"), Integer.parseInt(depthHistory), transField);
                    break;


                case CUSTOMERID:
                    LOGGER.info(
                            String.format(
                                    "Find Trans Field By History, with data: \n\tHistory by: [%s] \n\tHistory value: [%s] \n\tDepth: [%s] \n\tTransaction field: [%s]",
                                    CUSTOMERID.getName(), inputData.get("cifID"), Integer.parseInt(depthHistory), transField
                            )
                    );
                    fetchValue = this.currTransService.findTransFieldByHistory(CUSTOMERID.getName(), inputData.get("cifID"), Integer.parseInt(depthHistory), transField);
                    break;


                default:
                    LOGGER.error("Unknown history attribute: " + historyAttribute);
                    throw new IllegalArgumentException(
                            String.format(
                                    "Attribute %s is unknown",
                                    historyAttribute
                            )
                    );
            }

            if (fetchValue.isEmpty()) {
                return "''";
            }

            if (isNumeric(fetchValue)) {
                return fetchValue;
            }

            return "'" + fetchValue + "'";
        }
    }

    protected boolean isNumeric(String value) {
        return value.matches("\\d+");
    }

    protected abstract Object resolveValue(String depthHistory, String historyAttribute, String transField, Map<String, String> inputData, boolean ignoreType) throws Exception;
}
