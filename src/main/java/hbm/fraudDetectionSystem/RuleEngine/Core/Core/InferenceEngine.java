package hbm.fraudDetectionSystem.RuleEngine.Core.Core;


import hbm.fraudDetectionSystem.RuleEngine.Core.Core.Component.*;
import hbm.fraudDetectionSystem.RuleEngine.Core.Util.DSLPatternUtil;
import hbm.fraudDetectionSystem.RuleEngine.Core.Util.MVELParser;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule.Rule;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleBody.RuleBody;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Slf4j
public class InferenceEngine {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected boolean evaluateStatus = false;

    public void run(Rule rule, Map<String, String> transactionData) throws Exception {
        String formula = rule.getSFormula();

        LOGGER.info(
                String.format(
                        "Initial formula: [%s]",
                        formula
                )
        );

        for (RuleBody ruleBody : rule.getRuleBodies()) {
            LOGGER.info(
                    String.format(
                            "Start processing formula [%s]",
                            ruleBody.getConditionId()
                    )
            );

            LOGGER.info(
                    String.format(
                            "Detail formula: \n\tCondition id: [%s] \n\tType: [%s] \n\tExpression: [%s]",
                            ruleBody.getConditionId(), ruleBody.getCondition(), ruleBody.getDetailCondition()
                    )
            );

            switch (ruleBody.getCondition()) {
                case "CONSTANT":
                    ConstantEngine cEngine = new ConstantEngine(transactionData);
                    cEngine.run(ruleBody.getDetailCondition());
                    formula = DSLPatternUtil.assignEvaluatedStatus(formula, ruleBody.getConditionId(), cEngine.getEvaluateStatus());
                    LOGGER.info(
                            String.format(
                                    "Finish processing formula [%s], result: [%s]",
                                    ruleBody.getConditionId(),
                                    cEngine.getEvaluateStatus()
                            )
                    );
                    break;

                case "LOGICAL":
                    LogicalEngine lEngine = new LogicalEngine(transactionData);
                    lEngine.run(ruleBody.getDetailCondition());
                    formula = DSLPatternUtil.assignEvaluatedStatus(formula, ruleBody.getConditionId(), lEngine.getEvaluateStatus());
                    LOGGER.info(
                            String.format(
                                    "Finish processing formula [%s], result: [%s]",
                                    ruleBody.getConditionId(),
                                    lEngine.getEvaluateStatus()
                            )
                    );
                    break;
                case "ARITHMETIC":
                    ArithmeticEngine aEngine = new ArithmeticEngine(transactionData);
                    aEngine.run(ruleBody.getDetailCondition());
                    formula = DSLPatternUtil.assignEvaluatedStatus(formula, ruleBody.getConditionId(), aEngine.getEvaluateStatus());
                    LOGGER.info(
                            String.format(
                                    "Finish processing formula [%s], result: [%s]",
                                    ruleBody.getConditionId(),
                                    aEngine.getEvaluateStatus()
                            )
                    );
                    break;

                case "LIST":
                    ListEngine listEngine = new ListEngine(transactionData);
                    listEngine.run(ruleBody.getDetailCondition());
                    formula = DSLPatternUtil.assignEvaluatedStatus(formula, ruleBody.getConditionId(), listEngine.getEvaluateStatus());
                    LOGGER.info(
                            String.format(
                                    "Finish processing formula [%s], result: [%s]",
                                    ruleBody.getConditionId(),
                                    listEngine.getEvaluateStatus()
                            )
                    );
                    break;

                case "LIMIT":
                    LimitEngine limitEngine = new LimitEngine(transactionData);
                    limitEngine.run(ruleBody.getDetailCondition());
                    formula = DSLPatternUtil.assignEvaluatedStatus(formula, ruleBody.getConditionId(), limitEngine.getEvaluateStatus());
                    LOGGER.info(
                            String.format(
                                    "Finish processing formula [%s], result: [%s]",
                                    ruleBody.getConditionId(),
                                    limitEngine.getEvaluateStatus()
                            )
                    );
                    break;

                default:
                    break;
            }
        }

        LOGGER.info(
                String.format(
                        "Final formula: [%s]",
                        formula
                )
        );

        LOGGER.info(
                "Start evaluate formula..."
        );

        this.evaluateStatus = MVELParser.evaluateSFormula(formula);

        LOGGER.info(
                String.format(
                        "Final evaluate result: [%s]",
                        this.evaluateStatus
                )
        );
    }

    public boolean isRuleTriggered() {
        return evaluateStatus;
    }
}
