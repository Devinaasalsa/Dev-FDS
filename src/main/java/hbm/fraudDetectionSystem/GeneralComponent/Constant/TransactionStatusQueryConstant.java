package hbm.fraudDetectionSystem.GeneralComponent.Constant;

public class TransactionStatusQueryConstant {
    public static final String TRANSACTION_STATUS_DAILY_QUERY = "WITH daily_counts AS (\n" +
            "    SELECT\n" +
            "        curr_trans.fraud_flags,\n" +
            "        count(*) AS total_count\n" +
            "    FROM curr_trans\n" +
            "    WHERE\n" +
            "            curr_trans.fraud_flags IN (0, 1, 2)  -- Filter for the specified flags\n" +
            "      AND to_date(curr_trans.sysdate::text, 'YYYY-MM-DD'::text) = current_date\n" +
            "    GROUP BY curr_trans.fraud_flags\n" +
            "),\n" +
            "     fraud_summary AS (\n" +
            "         SELECT\n" +
            "             CASE\n" +
            "                 WHEN fraud_flags = 0 THEN 'Not Fraud'::text\n" +
            "                 WHEN fraud_flags = 1 THEN 'Suspicious'::text\n" +
            "                 WHEN fraud_flags = 2 THEN 'Fraud'::text\n" +
            "                 ELSE NULL::text\n" +
            "                 END AS fraud_category,\n" +
            "             sum(total_count) AS total_count\n" +
            "         FROM daily_counts\n" +
            "         group by daily_counts.fraud_flags\n" +
            "     )\n" +
            "SELECT\n" +
            "    fraud_summary.fraud_category,\n" +
            "    fraud_summary.total_count\n" +
            "FROM fraud_summary";

    public static final String TRANSACTION_STATUS_WEEKLY_QUERY = "WITH weekly_counts AS (\n" +
            "    SELECT\n" +
            "        curr_trans.fraud_flags,\n" +
            "        count(*) AS total_count\n" +
            "    FROM curr_trans\n" +
            "    WHERE\n" +
            "            curr_trans.fraud_flags IN (0, 1, 2)  -- Filter for the specified flags\n" +
            "      AND to_date(curr_trans.sysdate::text, 'YYYY-MM-DD'::text) BETWEEN current_date - INTERVAL '6 days' AND current_date\n" +
            "    GROUP BY curr_trans.fraud_flags\n" +
            "),\n" +
            "     fraud_summary AS (\n" +
            "         SELECT\n" +
            "             CASE\n" +
            "                 WHEN fraud_flags = 0 THEN 'Not Fraud'::text\n" +
            "                 WHEN fraud_flags = 1 THEN 'Suspicious'::text\n" +
            "                 WHEN fraud_flags = 2 THEN 'Fraud'::text\n" +
            "                 ELSE NULL::text\n" +
            "                 END AS fraud_category,\n" +
            "             sum(total_count) AS total_count\n" +
            "         FROM weekly_counts\n" +
            "         group by weekly_counts.fraud_flags\n" +
            "     )\n" +
            "SELECT\n" +
            "    fraud_summary.fraud_category,\n" +
            "    fraud_summary.total_count\n" +
            "FROM fraud_summary";

    public static final String TRANSACTION_STATUS_MONTHLY_QUERY = "WITH monthly_counts AS (\n" +
            "    SELECT\n" +
            "        curr_trans.fraud_flags,\n" +
            "        count(*) AS total_count\n" +
            "    FROM curr_trans\n" +
            "    WHERE\n" +
            "            curr_trans.fraud_flags IN (0, 1, 2)  -- Filter for the specified flags\n" +
            "      AND to_date(curr_trans.sysdate::text, 'YYYY-MM-DD'::text) BETWEEN current_date - INTERVAL '30 days' AND current_date\n" +
            "    GROUP BY curr_trans.fraud_flags\n" +
            "),\n" +
            "     fraud_summary AS (\n" +
            "         SELECT\n" +
            "             CASE\n" +
            "                 WHEN fraud_flags = 0 THEN 'Not Fraud'::text\n" +
            "                 WHEN fraud_flags = 1 THEN 'Suspicious'::text\n" +
            "                 WHEN fraud_flags = 2 THEN 'Fraud'::text\n" +
            "                 ELSE NULL::text\n" +
            "                 END AS fraud_category,\n" +
            "             sum(total_count) AS total_count\n" +
            "         FROM monthly_counts\n" +
            "         group by monthly_counts.fraud_flags\n" +
            "     )\n" +
            "SELECT\n" +
            "    fraud_summary.fraud_category,\n" +
            "    fraud_summary.total_count\n" +
            "FROM fraud_summary";
}
