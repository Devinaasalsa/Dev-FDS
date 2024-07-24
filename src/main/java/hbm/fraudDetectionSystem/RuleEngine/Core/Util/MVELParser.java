package hbm.fraudDetectionSystem.RuleEngine.Core.Util;

import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;

import java.util.Map;

public class MVELParser {

    public static boolean evaluateSFormula(String expression) {
        return (boolean) MVEL.eval(expression);
    }

    public static boolean evaluateConditionFormula(String expression, Map<String, Object> inputObjects){
        VariableResolverFactory resolverFactory = new MapVariableResolverFactory(inputObjects);
        return MVEL.evalToBoolean(expression,resolverFactory);
    }
}
