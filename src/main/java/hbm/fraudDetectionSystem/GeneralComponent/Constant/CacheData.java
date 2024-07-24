package hbm.fraudDetectionSystem.GeneralComponent.Constant;


import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleComparator.FieldRuleComparator;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;

public class CacheData {
    public static Map<String, FieldRuleComparator> CACHE_RULE_COMPARATOR;
    public static ScriptEngineManager SCRIPT_MANAGER = new ScriptEngineManager();
    public static ScriptEngine SCRIPT_ENGINE = SCRIPT_MANAGER.getEngineByName("js");
}
