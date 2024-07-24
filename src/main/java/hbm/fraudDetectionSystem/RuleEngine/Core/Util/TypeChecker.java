package hbm.fraudDetectionSystem.RuleEngine.Core.Util;

import java.util.Objects;

public class TypeChecker {
    public Object str(Object value) {
        return Objects.toString(value, "");
    }

    public Object cvt(Object value) {
        try {
            if (value instanceof Integer)
                return value;
            return Integer.parseInt((String) value);
        } catch (NumberFormatException e) {
            return value == null ? "" : value;
        }
    }

    public boolean isNum(Object value) {
        try {
            Integer.parseInt((String) value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
