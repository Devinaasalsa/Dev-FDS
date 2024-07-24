package hbm.fraudDetectionSystem.GeneralComponent.Constant;

public class CurrTransConstant {
    public static String convertCamelToSnake(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    public static final String GET_TRANSACTION_30DAYS_ORACLE_QUERY = "SELECT c.*, COALESCE(t.total_count, 0) AS rule_trigger\n" +
            "FROM curr_trans c\n" +
            "         LEFT JOIN (\n" +
            "    SELECT utrnno, COUNT(*) AS total_count\n" +
            "    FROM t_rule_triggered\n" +
            "    GROUP BY utrnno\n" +
            ") t ON c.utrnno = t.utrnno\n" +
            "WHERE TO_TIMESTAMP(c.\"sysdate\", 'YYYY-MM-DD HH24:MI:SS.FF3')  BETWEEN (SYSTIMESTAMP - INTERVAL '30' DAY) AND SYSTIMESTAMP\n" +
            "ORDER BY c.\"sysdate\" DESC";

    public static final String GET_TRANSACTION_30DAYS_QUERY = "SELECT c.*, count(trt.utrnno) as rule_trigger\n" +
            "FROM curr_trans c\n" +
            "left join t_rule_triggered trt ON c.utrnno = trt.utrnno\n" +
            "where to_date(c.sysdate, 'YYYY-MM-DD') BETWEEN to_date(?, 'YYYY-MM-DD') - INTERVAL '30 days' AND to_date(?, 'YYYY-MM-DD')\n" +
            "group by c.sysdate, c.utrnno\n" +
            "ORDER BY c.sysdate DESC";

    public static final String GET_TRANSACTION_30DAYS_BY_HPAN_ORACLE_QUERY = "SELECT c.*, COALESCE(t.total_count, 0) AS rule_trigger\n" +
            "FROM curr_trans c\n" +
            "         LEFT JOIN (\n" +
            "    SELECT utrnno, COUNT(*) AS total_count\n" +
            "    FROM t_rule_triggered\n" +
            "    GROUP BY utrnno\n" +
            ") t ON c.utrnno = t.utrnno\n" +
            "WHERE c.hpan = ? and TO_TIMESTAMP(c.\"sysdate\", 'YYYY-MM-DD HH24:MI:SS.FF3')  BETWEEN (SYSTIMESTAMP - INTERVAL '30' DAY) AND SYSTIMESTAMP\n" +
            "ORDER BY c.\"sysdate\" DESC";

    public static final String GET_TRANSACTION_30DAYS_BY_HPAN_QUERY = "SELECT c.*, COALESCE(t.total_count, 0) AS rule_trigger\n" +
            "FROM curr_trans c\n" +
            "LEFT JOIN (\n" +
            "    SELECT utrnno, COUNT(*) AS total_count\n" +
            "    FROM t_rule_triggered\n" +
            "    GROUP BY utrnno\n" +
            ") t ON c.utrnno = t.utrnno\n" +
            "WHERE c.hpan = ? and TO_TIMESTAMP(c.\"sysdate\", 'YYYY-MM-DD HH24:MI:SS.US') BETWEEN (CURRENT_TIMESTAMP - INTERVAL '30' DAY) AND CURRENT_TIMESTAMP\n" +
            "ORDER BY c.\"sysdate\" DESC";

    public static final String GET_TRANSACTION_30DAYS_BY_HPAN_CUST_ACCT1_QUERY(String alertDate) {
        return "SELECT c.*, COALESCE(t.total_count, 0) AS rule_trigger\n" +
                "FROM curr_trans c\n" +
                "         left join (select utrnno, count(*) as total_count\n" +
                "                    from t_rule_triggered\n" +
                "                    group by utrnno) t on c.utrnno = t.utrnno\n" +
                "WHERE hpan = ? and TO_TIMESTAMP(c.\"sysdate\", 'YYYY-MM-DD HH24:MI:SS.US') BETWEEN (TO_TIMESTAMP('" + alertDate + "', 'YYYY-MM-DD HH24:MI:SS.US') - INTERVAL '30' DAY) AND TO_TIMESTAMP('" + alertDate + "', 'YYYY-MM-DD HH24:MI:SS.US')\n" +
                "\n" +
                "UNION\n" +
                "\n" +
                "SELECT c.*, COALESCE(t.total_count, 0) AS rule_trigger\n" +
                "FROM curr_trans c\n" +
                "         left join (select utrnno, count(*) as total_count\n" +
                "                    from t_rule_triggered\n" +
                "                    group by utrnno) t on c.utrnno = t.utrnno\n" +
                "WHERE cif_id = ? and TO_TIMESTAMP(c.\"sysdate\", 'YYYY-MM-DD HH24:MI:SS.US') BETWEEN (TO_TIMESTAMP('" + alertDate + "', 'YYYY-MM-DD HH24:MI:SS.US') - INTERVAL '30' DAY) AND TO_TIMESTAMP('" + alertDate + "', 'YYYY-MM-DD HH24:MI:SS.US')\n" +
                "\n" +
                "UNION\n" +
                "\n" +
                "SELECT c.*, COALESCE(t.total_count, 0) AS rule_trigger\n" +
                "FROM curr_trans c\n" +
                "         left join (select utrnno, count(*) as total_count\n" +
                "                    from t_rule_triggered\n" +
                "                    group by utrnno) t on c.utrnno = t.utrnno\n" +
                "WHERE acct1 = ? and TO_TIMESTAMP(c.\"sysdate\", 'YYYY-MM-DD HH24:MI:SS.US') BETWEEN (TO_TIMESTAMP('" + alertDate + "', 'YYYY-MM-DD HH24:MI:SS.US') - INTERVAL '30' DAY) AND TO_TIMESTAMP('" + alertDate + "', 'YYYY-MM-DD HH24:MI:SS.US')\n" +
                "\n" +
                "ORDER BY sysdate desc";
    }

    //10 can be change to specific max depth
    public static String FIND_HISTORY_BY_TRANSFIELD_1_ORACLE_QUERY(String transField, String historyBy) {
        return String.format(
                "SELECT %s\n" +
                        "FROM (SELECT t.%s, ROW_NUMBER() OVER (ORDER BY \"sysdate\" DESC) AS rn\n" +
                        "      FROM curr_trans t\n" +
                        "      where %s = ?\n" +
                        "          fetch first 10 rows only)\n" +
                        "WHERE rn = ?",
                transField.toUpperCase(), transField.toUpperCase(), historyBy.toUpperCase()
        );
    }

    public static String FIND_HISTORY_BY_TRANSFIELD_1_QUERY(String transField, String historyBy) {
        return String.format(
                "SELECT %s\n" +
                        "FROM (\n" +
                        "    SELECT t.%s, ROW_NUMBER() OVER (ORDER BY \"sysdate\" DESC) AS rn\n" +
                        "    FROM curr_trans t\n" +
                        "    WHERE %s = ?\n" +
                        "    LIMIT 10\n" +
                        ") AS subquery\n" +
                        "WHERE rn = ?",
                transField.toUpperCase(), transField.toUpperCase(), historyBy.toUpperCase()
        );
    }

    public static String FIND_HISTORY_BY_TRANSFIELD_2_ORACLE_QUERY(String historyBy) {
        return String.format(
                "select cat.VALUE\n" +
                        "from CURR_ADDT_TRANS cat,\n" +
                        "     (SELECT *\n" +
                        "      FROM (SELECT t.*, ROW_NUMBER() OVER (ORDER BY \"sysdate\" DESC) AS rn\n" +
                        "            FROM curr_trans t\n" +
                        "            where %s = ?\n" +
                        "                fetch first 10 rows only)\n" +
                        "      WHERE rn = ?) ct\n" +
                        "where cat.UTRNNO = ct.UTRNNO\n" +
                        "and cat.ATTR = ?",
                historyBy.toUpperCase()
        );
    }

    public static String FIND_HISTORY_BY_TRANSFIELD_2_QUERY(String historyBy) {
        return String.format(
                "SELECT cat.VALUE\n" +
                        "FROM CURR_ADDT_TRANS cat\n" +
                        "JOIN (\n" +
                        "    SELECT *\n" +
                        "    FROM (\n" +
                        "        SELECT t.*, ROW_NUMBER() OVER (ORDER BY \"sysdate\" DESC) AS rn\n" +
                        "        FROM curr_trans t\n" +
                        "        WHERE %s = ?\n" +
                        "        LIMIT 10\n" +
                        "    ) AS subquery\n" +
                        "    WHERE rn = ?\n" +
                        ") AS ct\n" +
                        "ON cat.UTRNNO = ct.UTRNNO\n" +
                        "WHERE cat.ATTR = ?",
                historyBy.toUpperCase()
        );
    }

    public static String COUNT_MATCH_AGGREGATE_CT_AND_ATTR_CT(String filtrationJoinQuery, String filtrationQuery, String aggregate, String attr) {
        return String.format(
                "select count(ct.*)\n" +
                        "from curr_trans ct\n" +
                        filtrationJoinQuery +
                        "where ct.sysdate between ? and ?\n" +
                        "  and ct.%s = ?\n" +
                        "  and ct.%s = ?",
                convertCamelToSnake(aggregate), convertCamelToSnake(attr)
        ) + filtrationQuery;
    }

    public static String COUNT_MATCH_AGGREGATE_CAT_AND_ATTR_CT(String filtrationJoinQuery, String filtrationQuery, String aggregate, String attr) {
        return String.format(
                "select count(ct.*)\n" +
                        "from curr_trans ct\n" +
                        "         join curr_addt_trans cat on ct.utrnno = cat.utrnno\n" +
                        filtrationJoinQuery +
                        "where ct.sysdate between ? and ?\n" +
                        "  and cat.attr = '%s'\n" +
                        "  and cat.value = ?\n" +
                        "  and ct.%s = ?",
                aggregate, convertCamelToSnake(attr)
        ) + filtrationQuery;
    }

    public static String COUNT_MATCH_AGGREGATE_CT_AND_ATTR_CAT(String filtrationJoinQuery, String filtrationQuery, String aggregate, String attr) {
        return String.format(
                "select count(ct.*)\n" +
                        "from curr_trans ct\n" +
                        "         join curr_addt_trans cat on ct.utrnno = cat.utrnno\n" +
                        filtrationJoinQuery +
                        "where ct.sysdate between ? and ?\n" +
                        "  and ct.%s = ?\n" +
                        "  and cat.value = ?\n" +
                        "  and cat.attr = '%s'",
                convertCamelToSnake(aggregate), attr
        ) + filtrationQuery;
    }

    public static String COUNT_MATCH_AGGREGATE_CAT_AND_ATTR_CAT(String filtrationJoinQuery, String filtrationQuery, String aggregate, String attr) {
        return String.format(
                "select count(ct.*)\n" +
                        "from curr_trans ct\n" +
                        "         join curr_addt_trans cat on ct.utrnno = cat.utrnno\n" +
                        filtrationJoinQuery +
                        "where ct.sysdate between ? and ?\n" +
                        "  and cat.attr = '%s'\n" +
                        "  and cat.value = ?\n" +
                        "  and exists(\n" +
                        "        SELECT 1\n" +
                        "        FROM curr_addt_trans sub_cat\n" +
                        "        WHERE sub_cat.utrnno = cat.utrnno\n" +
                        "          AND sub_cat.attr = '%s'\n" +
                        "          AND sub_cat.value = ?\n" +
                        "    )",
                aggregate, attr
        ) + filtrationQuery;
    }

    public static String COUNT_DIFF_AGGREGATE_CT_AND_ATTR_CT(String filtrationJoinQuery, String filtrationQuery, String aggregate, String attr) {
        return String.format(
                "select count(distinct(ct.%s))\n" +
                        "from curr_trans ct\n" +
                        filtrationJoinQuery +
                        "where ct.sysdate between ? and ?\n" +
                        "  and ct.%s <> ?\n" +
                        "  and ct.%s = ?",
                convertCamelToSnake(aggregate), convertCamelToSnake(aggregate), convertCamelToSnake(attr)
        ) + filtrationQuery;
    }

    public static String COUNT_DIFF_AGGREGATE_CAT_AND_ATTR_CT(String filtrationJoinQuery, String filtrationQuery, String aggregate, String attr) {
        return String.format(
                "select count(distinct(cat.value))\n" +
                        "from curr_trans ct\n" +
                        "         join curr_addt_trans cat on ct.utrnno = cat.utrnno\n" +
                        filtrationJoinQuery +
                        "where ct.sysdate between ? and ?\n" +
                        "  and cat.attr = '%s'\n" +
                        "  and cat.value <> ?\n" +
                        "  and ct.%s = ?",
                aggregate, convertCamelToSnake(attr)
        ) + filtrationQuery;
    }

    public static String COUNT_DIFF_AGGREGATE_CT_AND_ATTR_CAT(String filtrationJoinQuery, String filtrationQuery, String aggregate, String attr) {
        return String.format(
                "select count(distinct(ct.%s))\n" +
                        "from curr_trans ct\n" +
                        "         join curr_addt_trans cat on ct.utrnno = cat.utrnno\n" +
                        filtrationJoinQuery +
                        "where ct.sysdate between ? and ?\n" +
                        "  and ct.%s <> ?\n" +
                        "  and cat.value = ?\n" +
                        "  and cat.attr = '%s'",
                convertCamelToSnake(aggregate), convertCamelToSnake(aggregate), attr
        ) + filtrationQuery;
    }

    public static String COUNT_DIFF_AGGREGATE_CAT_AND_ATTR_CAT(String filtrationJoinQuery, String filtrationQuery, String aggregate, String attr) {
        return String.format(
                "select count(distinct(cat.value))\n" +
                        "from curr_trans ct\n" +
                        "         join curr_addt_trans cat on ct.utrnno = cat.utrnno\n" +
                        filtrationJoinQuery +
                        "where ct.sysdate between ? and ?\n" +
                        "  and cat.attr = '%s'\n" +
                        "  and cat.value <> ?\n" +
                        "  and exists(\n" +
                        "        SELECT 1\n" +
                        "        FROM curr_addt_trans sub_cat\n" +
                        "        WHERE sub_cat.utrnno = cat.utrnno\n" +
                        "          AND sub_cat.attr = '%s'\n" +
                        "          AND sub_cat.value = ?\n" +
                        "    )",
                aggregate, attr
        ) + filtrationQuery;
    }

    public static String COUNT_AMOUNT_MATCH_AGGREGATE_CT_AND_ATTR_CT(String filtrationJoinQuery, String filtrationQuery, String aggregate, String attr) {
        return String.format(
                "select COALESCE(sum(cast(ct.amount as bigint)), 0)\n" +
                        "from curr_trans ct\n" +
                        filtrationJoinQuery +
                        "where ct.sysdate between ? and ?\n" +
                        "  and ct.%s = ?\n" +
                        "  and ct.%s = ?",
                convertCamelToSnake(aggregate), convertCamelToSnake(attr)
        ) + filtrationQuery;
    }

    public static String COUNT_AMOUNT_MATCH_AGGREGATE_CAT_AND_ATTR_CT(String filtrationJoinQuery, String filtrationQuery, String aggregate, String attr) {
        return String.format(
                "select COALESCE(sum(cast(ct.amount as bigint)), 0)\n" +
                        "from curr_trans ct\n" +
                        "         join curr_addt_trans cat on ct.utrnno = cat.utrnno\n" +
                        filtrationJoinQuery +
                        "where ct.sysdate between ? and ?\n" +
                        "  and cat.attr = '%s'\n" +
                        "  and cat.value = ?\n" +
                        "  and ct.%s = ?",
                aggregate, convertCamelToSnake(attr)
        ) + filtrationQuery;
    }

    public static String COUNT_AMOUNT_MATCH_AGGREGATE_CT_AND_ATTR_CAT(String filtrationJoinQuery, String filtrationQuery, String aggregate, String attr) {
        return String.format(
                "select COALESCE(sum(cast(ct.amount as bigint)), 0)\n" +
                        "from curr_trans ct\n" +
                        "         join curr_addt_trans cat on ct.utrnno = cat.utrnno\n" +
                        filtrationJoinQuery +
                        "where ct.sysdate between ? and ?\n" +
                        "  and ct.%s = ?\n" +
                        "  and cat.value = ?\n" +
                        "  and cat.attr = '%s'",
                convertCamelToSnake(aggregate), attr
        ) + filtrationQuery;
    }

    public static String COUNT_AMOUNT_MATCH_AGGREGATE_CAT_AND_ATTR_CAT(String filtrationJoinQuery, String filtrationQuery, String aggregate, String attr) {
        return String.format(
                "select COALESCE(sum(cast(ct.amount as bigint)), 0)\n" +
                        "from curr_trans ct\n" +
                        "         join curr_addt_trans cat on ct.utrnno = cat.utrnno\n" +
                        filtrationJoinQuery +
                        "where ct.sysdate between ? and ?\n" +
                        "  and cat.attr = '%s'\n" +
                        "  and cat.value = ?\n" +
                        "  and exists(\n" +
                        "        SELECT 1\n" +
                        "        FROM curr_addt_trans sub_cat\n" +
                        "        WHERE sub_cat.utrnno = cat.utrnno\n" +
                        "          AND sub_cat.attr = '%s'\n" +
                        "          AND sub_cat.value = ?\n" +
                        "    )",
                aggregate, attr
        ) + filtrationQuery;
    }

    public static String COUNT_AMOUNT_DIFF_AGGREGATE_CT_AND_ATTR_CT(String filtrationJoinQuery, String filtrationQuery, String aggregate, String attr) {
        return String.format(
                "select COALESCE(sum(cast(sub.amount as bigint)), 0)\n" +
                        "from (select ct.amount\n" +
                        "      from curr_trans ct\n" +
                        filtrationJoinQuery +
                        "where ct.sysdate between ? and ?\n" +
                        "        and ct.%s <> ?\n" +
                        "        and ct.%s = ?",
                convertCamelToSnake(aggregate), convertCamelToSnake(attr)
        ) + filtrationQuery + " order by ct.sysdate desc) sub";
    }

    public static String COUNT_AMOUNT_DIFF_AGGREGATE_CAT_AND_ATTR_CT(String filtrationJoinQuery, String filtrationQuery, String aggregate, String attr) {
        return String.format(
                "select COALESCE(sum(cast(sub.amount as bigint)), 0)\n" +
                        "from (select ct.amount\n" +
                        "      from curr_trans ct\n" +
                        "      join curr_addt_trans cat on ct.utrnno = cat.utrnno\n" +
                        filtrationJoinQuery +
                        "where ct.sysdate between ? and ?\n" +
                        "  and cat.attr = '%s'\n" +
                        "  and cat.value <> ?\n" +
                        "  and ct.%s = ?",
                aggregate, convertCamelToSnake(attr)
        ) + filtrationQuery + " order by ct.sysdate desc) sub";
    }

    public static String COUNT_AMOUNT_DIFF_AGGREGATE_CT_AND_ATTR_CAT(String filtrationJoinQuery, String filtrationQuery, String aggregate, String attr) {
        return String.format(
                "select COALESCE(sum(cast(sub.amount as bigint)), 0)\n" +
                        "from (select ct.amount\n" +
                        "      from curr_trans ct\n" +
                        "      join curr_addt_trans cat on ct.utrnno = cat.utrnno\n" +
                        filtrationJoinQuery +
                        "where ct.sysdate between ? and ?\n" +
                        "  and ct.%s <> ?\n" +
                        "  and cat.attr = '%s'\n" +
                        "  and cat.value = ?",
                convertCamelToSnake(aggregate), attr
        ) + filtrationQuery + " order by ct.sysdate desc) sub";
    }

    public static String COUNT_AMOUNT_DIFF_AGGREGATE_CAT_AND_ATTR_CAT(String filtrationJoinQuery, String filtrationQuery, String aggregate, String attr) {
        return String.format(
                "select COALESCE(sum(cast(sub.amount as bigint)), 0)\n" +
                        "from (select ct.amount from curr_trans ct\n" +
                        "         join curr_addt_trans cat on ct.utrnno = cat.utrnno\n" +
                        filtrationJoinQuery +
                        "where ct.sysdate between ? and ?\n" +
                        "  and cat.attr = '%s'\n" +
                        "  and cat.value <> ?\n" +
                        "  and exists(\n" +
                        "        SELECT 1\n" +
                        "        FROM curr_addt_trans sub_cat\n" +
                        "        WHERE sub_cat.utrnno = cat.utrnno\n" +
                        "          AND sub_cat.attr = '%s'\n" +
                        "          AND sub_cat.value = ?\n" +
                        "    )",
                aggregate, attr
        ) + filtrationQuery + " order by ct.sysdate desc) sub";
    }
}
