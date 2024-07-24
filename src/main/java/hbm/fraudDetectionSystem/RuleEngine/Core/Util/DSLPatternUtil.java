package hbm.fraudDetectionSystem.RuleEngine.Core.Util;



import hbm.fraudDetectionSystem.RuleEngine.Enum.DSLKeywordType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DSLPatternUtil {
    private static final Pattern DSL_PATTERN = Pattern.compile("\\$\\((\\w+);(\\d+);(\\w+);(\\w+)\\)"); //$(rulenamespace.keyword)
    private static final String SEMICOLON = ";";

    public static List<String> getListKeyword(String expression) {
        Matcher matcher = DSL_PATTERN.matcher(expression);
        List<String> listOfDslKeyword = new ArrayList<>();
        while (matcher.find()) {
            String group = matcher.group();
            listOfDslKeyword.add(group);
        }
        return listOfDslKeyword;
    }

    public static String extractKeyword(String keyword) {
        return keyword.substring(keyword.indexOf('(') + 1,
                keyword.indexOf(')'));
    }

    public static String getKeywordValue(String dslKeyword, DSLKeywordType keywordType) {
        return dslKeyword.split(SEMICOLON)[keywordType.ordinal()];
    }

    /*
        This will replace the formula to evaluated status by condition id
        S01 && S02 = true && false
        S01 -> true
        S02 -> false
     */
    public static String assignEvaluatedStatus(String formula, String key, boolean status) {
        return formula.replace(key, String.valueOf(status));
    }
}
