package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Enum.TypeField;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOComponent;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOMsg;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOFieldContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleComparator.FieldRuleComparator;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleValue.FieldRuleValue;

import java.util.List;

import static hbm.fraudDetectionSystem.GeneralComponent.Constant.CacheData.CACHE_RULE_COMPARATOR;
import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.checkCondition;
import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.LoggerHelper.*;

public class RuleFieldConfigurationHelper {

    public static int checkConfigurationIfMoreThan1(ISOComponent c, ISOComponent c1, List<ISOFieldContainer> filteredBitsActive, int conditionNumber, TypeField typeField) throws ISOException {
        switch (typeField) {
            case FIELD:
                return configurationMoreThan1FieldChecker(c, filteredBitsActive, conditionNumber);

            case SUB_FIELD:
                return configurationMoreThan1AndSubfieldChecker(c, c1, filteredBitsActive, conditionNumber);

            case HEADER_FIELD:
                return configurationMoreThan1AndHeaderFieldChecker(c, filteredBitsActive, conditionNumber);

            default:
                throw new ISOException(filteredBitsActive.get(0).getFieldId(), "Type Field is unknown.");
        }
    }

    public static int configurationMoreThan1FieldChecker(ISOComponent c, List<ISOFieldContainer> filteredBitsActive, int conditionNumber) throws ISOException {
        int index = 0;
        int priority = 1;
        boolean isConditionMatched = false;

        setMessageLogger(
                c,
                String.format(
                        "Checking Field: %s with total condition: %s",
                        filteredBitsActive.get(0).getFieldId(), filteredBitsActive.size()
                )
        );

        for (ISOFieldContainer getBitsActive : filteredBitsActive) {
            if (getBitsActive.getFieldCondition() != null) {
                setCalculateMessageLogger(
                        c,
                        String.format(
                                "Field: %s with Condition: %s and Priority: %s",
                                getBitsActive.getFieldId(), getBitsActive.getFieldCondition().getCondId(), priority)
                );

                if (isConditionMatch(getBitsActive.getFieldCondition(), (ISOMsg) c, null)) {
                    conditionNumber = index;
                    isConditionMatched = true;
                    break;
                }
            } else {
                setCalculateMessageLogger(
                        c,
                        String.format(
                                "Skip checking condition in Field: %s Priority: %s, due to condition is NULL.",
                                getBitsActive.getFieldId(), priority
                        )
                );
            }

            index++;
            priority++;
        }

        if (isConditionMatched) {
            setMessageLogger(c, String.format("Condition is match, will use configuration priority: %s.", conditionNumber + 1));
        } else {
            throw new ISOException(String.format(
                    "Field: %s is mandatory from BITMAP, need to set configuration for this field.",
                    filteredBitsActive.get(0).getFieldId()
            ));
        }

        return conditionNumber;
    }

    public static int configurationMoreThan1AndSubfieldChecker(ISOComponent c, ISOComponent c1, List<ISOFieldContainer> filteredBitsActive, int conditionNumber) throws ISOException {
        int index = 0;
        int priority = 1;
        boolean isConditionMatched = false;

        setMessageLogger(
                c,
                String.format(
                        "Checking Sub Field: %s with total condition: %s",
                        filteredBitsActive.get(0).getFieldId(), filteredBitsActive.size()
                )
        );

        for (ISOFieldContainer getBitsActive : filteredBitsActive) {
            if (getBitsActive.getFieldCondition() != null) {
                setCalculateMessageLogger(
                        c,
                        String.format(
                                "Field: %s with Condition: %s and Priority %s",
                                getBitsActive.getFieldId(), getBitsActive.getFieldCondition().getCondId(), priority)
                );

                if (!isConditionMatched) {
                    if (isConditionMatch(getBitsActive.getFieldCondition(), (ISOMsg) c, (ISOMsg) c1)) {
                        conditionNumber = index;
                        isConditionMatched = true;
                    }
                }
            } else {
                setCalculateMessageLogger(
                        c,
                        String.format(
                                "Skip checking condition in Field: %s Priority: %s, due to condition is NULL.",
                                getBitsActive.getFieldId(), priority
                        )
                );
            }

            index++;
            priority++;
        }

        if (isConditionMatched) {
            setMessageLogger(c, String.format("Condition is match, will use configuration priority: %s", conditionNumber + 1));
        } else {
            setMessageLogger(c, "There is no condition match, will use DEFAULT.");
        }

        return conditionNumber;
    }

    public static int configurationMoreThan1AndHeaderFieldChecker(ISOComponent c, List<ISOFieldContainer> filteredBitsActive, int conditionNumber) throws ISOException {
        int index = 0;
        int priority = 1;
        boolean isConditionMatched = false;

        setMessageLogger(
                c,
                String.format(
                        "Checking Header Field: %s with total condition: %s",
                        filteredBitsActive.get(0).getFieldId(), filteredBitsActive.size()
                )
        );

        for (ISOFieldContainer getBitsActive : filteredBitsActive) {
            if (getBitsActive.getFieldCondition() != null) {
                setCalculateMessageLogger(
                        c,
                        String.format(
                                "Header Field: %s with Condition: %s and Priority: %s",
                                getBitsActive.getFieldId(), getBitsActive.getFieldCondition().getCondId(), priority)
                );

                if (!isConditionMatched) {
                    if (isConditionMatch(getBitsActive.getFieldCondition(), (ISOMsg) c, null)) {
                        conditionNumber = index;
                        isConditionMatched = true;
                    }
                }
            } else {
                setCalculateMessageLogger(
                        c,
                        String.format(
                                "Skip checking condition in Header Field: %s Priority: %s, due to condition is NULL.",
                                getBitsActive.getFieldId(), priority
                        )
                );
            }

            index++;
            priority++;
        }

        if (isConditionMatched) {
            setMessageLogger(c, String.format("Condition is match, will use configuration priority: %s", conditionNumber + 1));
        } else {
            setMessageLogger(c, "There is no condition match, will use DEFAULT.");
        }

        return conditionNumber;
    }

    public static boolean checkConfigurationIfOnly1(ISOComponent c, ISOComponent c1, ISOFieldContainer bitActive, TypeField typeField) throws ISOException {
        switch (typeField) {
            case FIELD:
                return configurationOnly1FieldChecker(c, bitActive);
            case SUB_FIELD:
                return configurationOnly1SubFieldChecker(c, c1, bitActive);
            case HEADER_FIELD:
                return configurationOnly1HeaderFieldChecker(c, bitActive);
            default:
                throw new ISOException(bitActive.getFieldId(), "Type Field is unknown.");
        }
    }

    public static boolean configurationOnly1FieldChecker(ISOComponent c, ISOFieldContainer bitActive) throws ISOException {
        boolean isConditionMatched;

        setMessageLogger(
                c,
                String.format(
                        "Checking Field: %s with condition id: %s",
                        bitActive.getFieldId(), bitActive.getFieldCondition().getCondId()
                )
        );

        if (bitActive.getFieldCondition() != null) {
            isConditionMatched = isConditionMatch(bitActive.getFieldCondition(), (ISOMsg) c, null);

            if (isConditionMatched) {
                setMessageLogger(
                        c,
                        String.format(
                                "Condition is match, will use condition id: %s.",
                                bitActive.getFieldCondition().getCondId()
                        )
                );
            } else {
                throw new ISOException(String.format(
                        "Field: %s is mandatory from BITMAP, need to set configuration for this field.",
                        bitActive.getFieldId()
                ));
            }
        } else {
            throw new ISOException(String.format(
                    "Field: %s is mandatory from BITMAP, need to set configuration for this field.",
                    bitActive.getFieldId()
            ));
        }

        return true;
    }

    public static boolean configurationOnly1SubFieldChecker(ISOComponent c, ISOComponent c1, ISOFieldContainer bitActive) throws ISOException {
        boolean isConditionMatched = false;

        setMessageLogger(
                c,
                String.format(
                        "Checking Sub Field: %s with condition id: %s",
                        bitActive.getFieldId(), bitActive.getFieldCondition().getCondId()
                )
        );

        if (bitActive.getFieldCondition() != null) {
            isConditionMatched = isConditionMatch(bitActive.getFieldCondition(), (ISOMsg) c, null);

            if (isConditionMatched) {
                setMessageLogger(
                        c,
                        String.format(
                                "Condition is match, will use condition id: %s.",
                                bitActive.getFieldCondition().getCondId()
                        )
                );
            } else {
                setMessageLogger(
                        c,
                        String.format(
                                "There is no condition match, will skip process field: %s.",
                                bitActive.getFieldId()
                        )
                );
            }
        } else {
            setCalculateMessageLogger(
                    c,
                    String.format(
                            "Skip checking condition in Field: %s, due to condition is NULL.",
                            bitActive.getFieldId()
                    )
            );
        }

        return isConditionMatched;
    }

    public static boolean configurationOnly1HeaderFieldChecker(ISOComponent c, ISOFieldContainer bitActive) throws ISOException {
        boolean isConditionMatched = false;

        setMessageLogger(
                c,
                String.format(
                        "Checking Header Field: %s with condition id: %s",
                        bitActive.getFieldId(), bitActive.getFieldCondition().getCondId()
                )
        );

        if (bitActive.getFieldCondition() != null) {
            isConditionMatched = isConditionMatch(bitActive.getFieldCondition(), (ISOMsg) c, null);

            if (isConditionMatched) {
                setMessageLogger(
                        c,
                        String.format(
                                "Condition is match, will use condition id: %s.",
                                bitActive.getFieldCondition().getCondId()
                        )
                );
            } else {
                setMessageLogger(
                        c,
                        String.format(
                                "There is no condition match, will skip unpack header field: %s.",
                                bitActive.getFieldId()
                        )
                );
            }
        } else {
            setCalculateMessageLogger(
                    c,
                    String.format(
                            "Skip checking condition in Header Field: %s, due to condition is NULL.",
                            bitActive.getFieldId()
                    )
            );
        }

        return isConditionMatched;
    }

    protected static boolean isConditionMatch(FieldRuleCondition fieldRuleCondition, ISOMsg c, ISOMsg c1) throws ISOException {
        String convertedCondition = convertCondition(fieldRuleCondition.getCondition(), c, c1);
        boolean lastResult = checkCondition(convertedCondition);
        c.setLoggerMessage(
                String.format(
                        "Calculate: Result Formula: %s",
                        lastResult
                )
        );
        return lastResult;
    }

    protected static String convertCondition(String condition, ISOMsg c, ISOMsg c1) {
        String resultCondition = "";

        c.setLoggerMessage(
                String.format(
                        "Calculate: Condition Formula: %s",
                        condition
                )
        );

        for (String sepCondition : condition.split("[^\\w']+")) {
            if (!sepCondition.isBlank()) {
                String comparatorId = sepCondition.replaceAll("[^0-9]", "");
                FieldRuleComparator fetchedComparator = fetchComparatorFromCacheData(comparatorId);
                if (resultCondition.isBlank())
                    resultCondition = condition.replace(comparatorId, compareValue(fetchedComparator, c, c1).toString());
                else
                    resultCondition = resultCondition.replace(comparatorId, compareValue(fetchedComparator, c, c1).toString());
            }
        }

        if (!resultCondition.isBlank())
            c.setLoggerMessage(
                    String.format(
                            "Calculate: Result Formula: %s",
                            resultCondition
                    )
            );

        return resultCondition;
    }

    protected static FieldRuleComparator fetchComparatorFromCacheData(String comparatorId) {
        return CACHE_RULE_COMPARATOR.get(comparatorId);
    }

    protected static Boolean compareValue(FieldRuleComparator fieldRuleComparator, ISOMsg c, ISOMsg c1) {
        boolean fixValue = false;
        c.setLoggerMessage(
                String.format(
                        "Calculate: Formula: %s",
                        fieldRuleComparator.getComparatorId()
                )
        );
        switch (fieldRuleComparator.getOperator()) {
            case "=":
                String value1 = getValueByValueType(fieldRuleComparator.getValueId1(), c, c1);
                String value2 = getValueByValueType(fieldRuleComparator.getValueId2(), c, c1);

                c.setLoggerMessage(
                        String.format(
                                "Calculate: value1: [%s] == value2: [%s]",
                                replaceNull(value1), replaceNull(value2)
                        )
                );

                if (value1 != null && value2 != null) {
                    if (value1.endsWith("*")) {
                        fixValue = valueEndWithPrefix(value1, value2);
                    } else if (value1.startsWith("*")) {
                        fixValue = valueStartWithPrefix(value1, value2);
                    } else if (value2.endsWith("*")) {
                        fixValue = valueEndWithPrefix(value2, value1);
                    } else if (value2.startsWith("*")) {
                        fixValue = valueStartWithPrefix(value2, value1);
                    } else if (value1.contains("*")) {
                        fixValue = valueHavePrefixInTheMiddle(value1, value2);
                    } else if (value2.contains("*")) {
                        fixValue = valueHavePrefixInTheMiddle(value2, value1);
                    } else fixValue = value1.equals(value2);
                }

                if (fixValue) {
                    c.setLoggerMessage(
                            "Calculate: value are matched"
                    );
                } else
                    c.setLoggerMessage("Calculate: value aren't matched");
                break;

            default:
                break;
        }
        return fixValue;
    }

    protected static String getValueByValueType(FieldRuleValue fieldRuleValue, ISOMsg c, ISOMsg c1) {
        String fixValue;
        switch (fieldRuleValue.getTypeId().getTypeId().intValue()) {
            case 1:
                fixValue = fieldRuleValue.getValue();
                break;

            case 2:
                String fieldId;
                String subFieldId;
                if (fieldRuleValue.getValue().contains(".")) {
                    fieldId = fieldRuleValue.getValue().split("\\.")[0];
                    subFieldId = fieldRuleValue.getValue().split("\\.")[1];

                    if (c.getString(fieldId) != null) {
                        fixValue = c.getString(fieldRuleValue.getValue());
                    } else {
                        fixValue = c1.getString(subFieldId);
                    }
                } else {
                    String value = c.getString(fieldRuleValue.getValue());
                    if (value.contains("Object")) {
                        fixValue = "null";
                        break;
                    }
                    fixValue = value;
                }
                break;

            default:
                throw new IllegalArgumentException("Value Type is unknown");
        }

        return fixValue;
    }

    protected static boolean valueEndWithPrefix(String value1, String value2) {
        String removedPrefixValue = value1.replace("*", "");
        return removedPrefixValue.equals(value2.substring(0, removedPrefixValue.length()));
    }

    protected static boolean valueStartWithPrefix(String value1, String value2) {
        String removedPrefixValue = value1.replace("*", "");
        return removedPrefixValue.equals(value2.substring(Math.max(value2.length() - removedPrefixValue.length(), 0)));
    }

    protected static boolean valueHavePrefixInTheMiddle(String value1, String value2) {
        String start = value1.split("\\*")[0];
        String end = value1.split("\\*")[1];
        return start.equals(value2.substring(0, start.length())) && end.equals(value2.substring(Math.max(value2.length() - end.length(), 0)));
    }
}
