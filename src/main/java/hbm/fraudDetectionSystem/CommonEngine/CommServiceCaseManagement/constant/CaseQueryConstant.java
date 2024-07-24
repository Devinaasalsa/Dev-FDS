package hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.constant;

public class CaseQueryConstant {
    public static final String FIND_BY_ORDER_BY_LASTUPDATE =
            "SELECT\n" +
            "    tai.case_id,\n" +
            "    tai.action_date,\n" +
            "    tai.action_type,\n" +
            "    tai.case_comment,\n" +
            "    tai.clasification_type,\n" +
            "    tai.clasified_comment,\n" +
            "    tai.classified_date,\n" +
            "    tai.hpan AS \"case_hpan\",\n" +
            "    tai.initiator,\n" +
            "    tai.is_actioned,\n" +
            "    tai.is_classified,\n" +
            "    tai.is_forwarded,\n" +
            "    tai.is_locked,\n" +
            "    tai.last_update,\n" +
            "    tai.locked_by,\n" +
            "    tai.utrnno AS \"case_utrnno\",\n" +
            "    tai.forwarded_to,\n" +
            "    ct.*\n" +
            "FROM\n" +
            "    t_alerts_investigation tai, curr_trans ct where\n" +
            "tai.utrnno = ct.utrnno";

    public static final String FIND_BY_LOCKEDBY_USERNAME = "SELECT\n" +
            "    tai.case_id,\n" +
            "    tai.action_date,\n" +
            "    tai.action_type,\n" +
            "    tai.case_comment,\n" +
            "    tai.clasification_type,\n" +
            "    tai.clasified_comment,\n" +
            "    tai.classified_date,\n" +
            "    tai.hpan AS \"case_hpan\",\n" +
            "    tai.initiator,\n" +
            "    tai.is_actioned,\n" +
            "    tai.is_classified,\n" +
            "    tai.is_forwarded,\n" +
            "    tai.is_locked,\n" +
            "    tai.last_update,\n" +
            "    tai.locked_by,\n" +
            "    tai.utrnno AS \"case_utrnno\",\n" +
            "    tai.forwarded_to,\n" +
            "    ct.*\n" +
            "   FROM t_alerts_investigation tai, Curr_Trans ct where tai.utrnno = ct.utrnno OR tai.forwarded_to = :userId ORDER BY CASE WHEN locked_by = :username THEN 0 ELSE 1 END, last_update DESC";

    public static final String FIND_BY_LOCKEDBY_USERNAME_ORACLE = "SELECT tai.case_id,\n" +
            "       tai.action_date,\n" +
            "       tai.action_type,\n" +
            "       tai.case_comment,\n" +
            "       tai.clasification_type,\n" +
            "       tai.clasified_comment,\n" +
            "       tai.classified_date,\n" +
            "       tai.hpan   AS case_hpan,\n" +
            "       tai.initiator,\n" +
            "       tai.is_actioned,\n" +
            "       tai.is_classified,\n" +
            "       tai.is_forwarded,\n" +
            "       tai.is_locked,\n" +
            "       tai.last_update,\n" +
            "       tai.locked_by,\n" +
            "       tai.utrnno AS case_utrnno,\n" +
            "       tai.forwarded_to,\n" +
            "       ct.*\n" +
            "FROM t_alerts_investigation tai\n" +
            "         LEFT JOIN\n" +
            "     Curr_Trans ct ON tai.utrnno = ct.utrnno\n" +
            "WHERE (tai.forwarded_to = :userId or tai.forwarded_to is null)\n" +
            "and (tai.locked_by = :username or tai.locked_by is null)\n" +
            "and tai.is_classified is false\n" +
            "ORDER BY case_id;";

    public static final String FIND_CASE_BY_LOCKEDBY_USERNAME = "SELECT\n" +
            "    tai.case_id,\n" +
            "    tai.action_date,\n" +
            "    tai.action_type,\n" +
            "    tai.case_comment,\n" +
            "    tai.clasification_type,\n" +
            "    tai.clasified_comment,\n" +
            "    tai.classified_date,\n" +
            "    tai.hpan AS \"case_hpan\",\n" +
            "    tai.initiator,\n" +
            "    tai.is_actioned,\n" +
            "    tai.is_classified,\n" +
            "    tai.is_forwarded,\n" +
            "    tai.is_locked,\n" +
            "    tai.last_update,\n" +
            "    tai.locked_by,\n" +
            "    tai.utrnno AS \"case_utrnno\",\n" +
            "    tai.forwarded_to,\n" +
            "    ct.*\n" +
            "   FROM t_alerts_investigation tai, Curr_Trans ct where tai.locked_by = :initiator and ct.utrnno = tai.utrnno";

    public static final String FIND_CASE_BY_LOCKEDBY_USERNAME_ORACLE = "SELECT\n" +
            "    tai.case_id,\n" +
            "    tai.action_date,\n" +
            "    tai.action_type,\n" +
            "    tai.case_comment,\n" +
            "    tai.clasification_type,\n" +
            "    tai.clasified_comment,\n" +
            "    tai.classified_date,\n" +
            "    tai.hpan AS case_hpan,\n" +
            "    tai.initiator,\n" +
            "    tai.is_actioned,\n" +
            "    tai.is_classified,\n" +
            "    tai.is_forwarded,\n" +
            "    tai.is_locked,\n" +
            "    tai.last_update,\n" +
            "    tai.locked_by,\n" +
            "    tai.utrnno AS case_utrnno,\n" +
            "    tai.forwarded_to,\n" +
            "    ct.*\n" +
            "   FROM t_alerts_investigation tai, Curr_Trans ct where tai.locked_by = :initiator and ct.utrnno = tai.utrnno";

    public static final String REPORT_ALERT_STATISTIC_BY_RULES = "SELECT tfr.rule_name, tai.hpan, crm.rule_id, COUNT(crm.case_id)\n" +
            "FROM case_rules_map crm \n" +
            "JOIN t_rule tfr ON tfr.rule_id = crm.rule_id\n" +
            "JOIN t_alerts_investigation tai ON tai.case_id = crm.case_id\n" +
            "where tai.alert_date between :reportStartDate and :reportEndDate \n" +
            "GROUP BY crm.rule_id, tfr.rule_name, tai.hpan;";

    public static final String REPORT_ALERT_STATISTIC_TOTAL = "select date_trunc('day', \"alert_date\") as \"alert_date\", count(\"alert_date\") as \"total_alerts\" from t_alerts_investigation\n" +
            "where alert_date between :reportStartDate and :reportEndDate group by date_trunc('day', \"alert_date\");";

    public static final String REPORT_NOT_ALERTED_ALERTED = "WITH alerted_cases AS (\n" +
            "    SELECT COUNT(DISTINCT tai.case_id) AS not_alerted \n" +
            "    FROM t_alerts_investigation tai\n" +
            "    WHERE tai.case_id NOT IN (\n" +
            "        select distinct(case_id) from case_rules_map\n" +
            "    )\n" +
            "\tAND tai.alert_date between :reportStartDate AND :reportEndDate\n" +
            ")\n" +
            "SELECT alerted_cases.not_alerted,COUNT(*) AS alerted FROM alerted_cases, t_alerts_investigation tai\n" +
            "WHERE tai.alert_date BETWEEN :reportStartDate AND :reportEndDate GROUP BY alerted_cases.not_alerted;";
}
