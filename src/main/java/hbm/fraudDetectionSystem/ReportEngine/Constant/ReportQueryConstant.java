package hbm.fraudDetectionSystem.ReportEngine.Constant;

public class ReportQueryConstant {

    public static final String REPORT_ALERT_STATISTIC_BY_RULE_QUERY = "SELECT trt.rule_id, tr.rule_name, COUNT(tai.utrnno) AS alert_count FROM t_rule_triggered trt JOIN t_rule tr ON trt.rule_id = tr.rule_id JOIN t_alerts_investigation tai ON trt.utrnno = tai.utrnno where tai.alert_date between ? and ? GROUP BY trt.rule_id, tr.rule_name";

    public static final String REPORT_ALERT_STATISTIC_TOTAL_QUERY = "select date_trunc('day', alert_date) as alert_date, count(alert_date) as total_alert\n" +
            "from t_alerts_investigation tai\n" +
            "where tai.alert_date between ? and ?\n" +
            "group by date_trunc('day', alert_date)";

    public static final String REPORT_ALERT_STATISTIC_TOTAL_ORACLE_QUERY = "select trunc(alert_date, 'DD') as alert_date, count(alert_date) as total_alert\n" +
            "from t_alerts_investigation tai\n" +
            "where tai.alert_date between ? and ?\n" +
            "group by trunc(alert_date, 'DD')";

    public static final String REPORT_USER_ACTIVITY_HISTORY_QUERY = "select tuc.username,\n" +
            "       COALESCE(sum(apbu.alert_total), 0) as alert_processed_by_user,\n" +
            "       COALESCE(sum(cmfbu.fraudulent_total), 0)      as card_marked_fraudulent_by_user\n" +
            "from t_user_config tuc\n" +
            "         left join (select username, sum(total) as alert_total\n" +
            "                    from alert_processed_by_user\n" +
            "                    where date between ? and ?\n" +
            "                    group by username) apbu ON tuc.username = apbu.username\n" +
            "         left join (select username, sum(total) as fraudulent_total\n" +
            "                    from card_marked_fraudulent_by_user\n" +
            "                    where date between ? and ?\n" +
            "                    group by username) cmfbu ON tuc.username = cmfbu.username\n" +
            "group by tuc.username;";

    public static final String REPORT_USER_ACTIVITY_HISTORY_ORACLE_QUERY = "SELECT tuc.username,\n" +
            "       COALESCE(sum(apbu.alert_total), 0) AS alert_processed_by_user,\n" +
            "       COALESCE(sum(cmfbu.fraudulent_total), 0) AS card_marked_fraudulent_by_user\n" +
            "FROM t_user_config tuc\n" +
            "LEFT JOIN (SELECT username, sum(total) AS alert_total\n" +
            "           FROM alert_processed_by_user\n" +
            "           WHERE \"DATE\" BETWEEN ? AND ?\n" +
            "           GROUP BY username) apbu ON tuc.username = apbu.username\n" +
            "LEFT JOIN (SELECT username, sum(total) AS fraudulent_total\n" +
            "           FROM card_marked_fraudulent_by_user\n" +
            "           WHERE \"DATE\" BETWEEN ? AND ?\n" +
            "           GROUP BY username) cmfbu ON tuc.username = cmfbu.username\n" +
            "GROUP BY tuc.username";

    public static final String REPORT_DECLINE_ONLINE_QUERY = "select date_trunc('day', cast(sysdate as timestamp)) as auth_date,\n" +
            "       resp_code,\n" +
            "       resp_code_desc,\n" +
            "       currency,\n" +
            "       count(resp_code)                              as total\n" +
            "from curr_trans\n" +
            "where resp_code != '-1'\n" +
            "  and sysdate between ? and ?\n" +
            "group by date_trunc('day', cast(sysdate as timestamp)), resp_code, resp_code_desc, currency;";

    public static final String REPORT_DECLINE_ONLINE_ORACLE_QUERY = "select trunc(TO_TIMESTAMP(\"sysdate\", 'YYYY-MM-DD HH24:MI:SS.FF'), 'DD') as auth_date,\n" +
            "       resp_code,\n" +
            "       resp_code_desc,\n" +
            "       currency,\n" +
            "       count(resp_code)                              as total\n" +
            "from curr_trans\n" +
            "where resp_code != '00'\n" +
            "  and TO_TIMESTAMP(\"sysdate\", 'YYYY-MM-DD HH24:MI:SS.FF') between ? and ?\n" +
            "group by trunc(TO_TIMESTAMP(\"sysdate\", 'YYYY-MM-DD HH24:MI:SS.FF'), 'DD'), resp_code, resp_code_desc, currency";

    public static final String REPORT_CARD_HAD_TRANS_ON_FRAUDULENT_TERMINAL_QUERY = "select hpan, utrnno, terminal_id, sysdate as trans_date, amount, resp_code, currency\n" +
            "from curr_trans ct\n" +
            "        join (select distinct tflv.value\n" +
            "                    from t_fraud_list_value tflv, t_fraud_list tfl\n" +
            "                    where tflv.list_id = tfl.list_id and tfl.entity_type_id = 3) fv ON ct.terminal_id = fv.value\n" +
            "where sysdate between ? and ? order by sysdate desc;";

    public static final String REPORT_CARD_HAD_TRANS_ON_FRAUDULENT_TERMINAL_ORACLE_QUERY = "select hpan, utrnno, terminal_id, sysdate as trans_date, amount, resp_code, currency\n" +
            "from curr_trans ct\n" +
            "        join (select distinct tflv.value\n" +
            "                    from t_fraud_list_value tflv, t_fraud_list tfl\n" +
            "                    where tflv.list_id = tfl.list_id and tfl.entity_type_id = 3) fv ON ct.terminal_id = fv.value\n" +
            "where TO_TIMESTAMP(\"sysdate\", 'YYYY-MM-DD HH24:MI:SS.FF') between ? and ? order by TO_TIMESTAMP(\"sysdate\", 'YYYY-MM-DD HH24:MI:SS.FF') desc";

    public static final String REPORT_RULE_EFFECTIVENESS_QUERY = "select tr.rule_id, tr.rule_name, count(*) as total_fraud_trans_triggered\n" +
            "from t_rule tr\n" +
            "         join (select trt.id, trt.rule_id, trt.utrnno\n" +
            "               from curr_trans ct,\n" +
            "                    t_rule_triggered trt\n" +
            "               where ct.fraud_flags = 2\n" +
            "                 and ct.utrnno = trt.utrnno\n" +
            "                 and ct.sysdate between ? and ?) trt\n" +
            "              on tr.rule_id = trt.rule_id\n" +
            "group by tr.rule_id, tr.rule_name";

    public static final String REPORT_RULE_EFFECTIVENESS_ORACLE_QUERY = "SELECT\n" +
            "    tr.rule_id,\n" +
            "    tr.rule_name,\n" +
            "    COUNT(*) AS total_fraud_trans_triggered\n" +
            "FROM\n" +
            "    t_rule tr\n" +
            "JOIN\n" +
            "    (SELECT trt.id, trt.rule_id, trt.utrnno\n" +
            "     FROM curr_trans ct\n" +
            "     JOIN t_rule_triggered trt ON ct.utrnno = trt.utrnno\n" +
            "     WHERE ct.fraud_flags = 2\n" +
            "       AND TO_TIMESTAMP(ct.\"sysdate\", 'YYYY-MM-DD HH24:MI:SS.FF') BETWEEN ? and ?\n" +
            "    ) trt ON tr.rule_id = trt.rule_id\n" +
            "GROUP BY\n" +
            "    tr.rule_id, tr.rule_name";

    public static final String REPORT_CARD_ADDED_TO_STOP_LIST_QUERY = "select tfbl.value,\n" +
            "       EXTRACT(DAY FROM cast(tfbl.date_out as timestamp) - cast(tfbl.date_in as timestamp)) AS blocking_time,\n" +
            "       tuc.user_id,\n" +
            "       tuc.username,\n" +
            "       count(*) filter ( where tfbl.value = tai.hpan ) as total_alert_handled\n" +
            "from t_fraud_black_list tfbl, t_user_config tuc, t_alerts_investigation tai\n" +
            "where lower(tfbl.entity_type) = lower('hpan')\n" +
            "  and tuc.id = tfbl.initiator_id\n" +
            "  and tfbl.date_in between ? and ?\n" +
            "  and tfbl.date_out between ? and ?\n" +
            "group by tfbl.value, blocking_time, tuc.user_id, tuc.username;";

    public static final String REPORT_CARD_ADDED_TO_STOP_LIST_ORACLE_QUERY = "SELECT\n" +
            "    tfbl.value,\n" +
            "    EXTRACT(DAY FROM TO_TIMESTAMP(tfbl.date_out, 'YYYY-MM-DD HH24:MI:SS') - TO_TIMESTAMP(tfbl.date_in, 'YYYY-MM-DD HH24:MI:SS')) AS blocking_time,\n" +
            "    tuc.user_id,\n" +
            "    tuc.username,\n" +
            "    COUNT(CASE WHEN tfbl.value = tai.hpan THEN 1 ELSE NULL END) AS total_alert_handled\n" +
            "FROM\n" +
            "    t_fraud_black_list tfbl\n" +
            "JOIN\n" +
            "    t_user_config tuc ON tuc.id = tfbl.initiator_id\n" +
            "JOIN\n" +
            "    t_alerts_investigation tai ON LOWER(tfbl.entity_type) = LOWER('hpan')\n" +
            "WHERE\n" +
            "    tfbl.date_in BETWEEN ? AND ?\n" +
            "    AND tfbl.date_out BETWEEN ? AND ?\n" +
            "GROUP BY\n" +
            "    tfbl.value, EXTRACT(DAY FROM TO_TIMESTAMP(tfbl.date_out, 'YYYY-MM-DD HH24:MI:SS') - TO_TIMESTAMP(tfbl.date_in, 'YYYY-MM-DD HH24:MI:SS')), tuc.user_id, tuc.username";
}
