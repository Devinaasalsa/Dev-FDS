package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.ActionHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hbm.fraudDetectionSystem.GeneralComponent.Utility.ApplicationContext;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils.JSONFieldHeaderDictionary;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldAction.JSONFieldAction;
import hbm.fraudDetectionSystem.TransactionEngine.Exception.TransactionEngineException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionInvocationTargetException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JSONActionHandler {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    public JsonNode run(ObjectNode m, ObjectNode pm, String value, JSONFieldHeaderDictionary fConfig) throws TransactionEngineException, JsonParseException {
        JsonNode aResult = m;

        List<String> actionList = fConfig.getActions()
                .stream()
                .map(v1 -> v1.getType().getExpression())
                .collect(Collectors.toList());

        LOGGER.info(
                String.format(
                        "List action: %s",
                        actionList
                )
        );

        for (JSONFieldAction action : fConfig.getActions()) {
            LOGGER.info(
                    String.format(
                            "Execute action [%s]",
                            action.getType().getExpression()
                    )
            );

            LOGGER.info("Prepare data model...");

            ExpressionParser ep = new SpelExpressionParser();

            StandardEvaluationContext sec = new StandardEvaluationContext();
            sec.setVariable("actionProcessor", this.getActionProcessorByName(action.getType().getExpression()));
            sec.setVariable("on", aResult);
            sec.setVariable("pn", pm);
            sec.setVariable("value", value);
            sec.setVariable("fConfig", fConfig);
            sec.setVariable("args", action.getArgs());

            Expression expression = ep.parseExpression("#actionProcessor.run(#on, #pn, #value, #fConfig, #args)");
            aResult = this.getExpressionResult(expression, sec);

            if (aResult != null)
                value = aResult.asText();

            LOGGER.info(
                    String.format(
                            "Finish execute action [%s]",
                            action.getType().getExpression()
                    )
            );
        }

        return aResult;
    }

    protected JSONActionProcessor getActionProcessorByName(String name) {
        return ApplicationContext.getBean(name, JSONActionProcessor.class);
    }

    protected JsonNode getExpressionResult(Expression expression, StandardEvaluationContext sec) throws TransactionEngineException {
        try {
            return expression.getValue(sec, JsonNode.class);
        } catch (ExpressionInvocationTargetException e) {
            Throwable rootCause = e.getCause();

            if (rootCause instanceof TransactionEngineException) {
                throw (TransactionEngineException) rootCause;
            } else if (rootCause instanceof JsonParseException) {
                throw new TransactionEngineException("Unexpected error in json message", e);
            } else {
                throw e;
            }
        } catch (Exception e) {
            throw e;
        }
    }
}