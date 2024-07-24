package hbm.fraudDetectionSystem.ReportEngine.SpringLogic.StaticReport;

import hbm.fraudDetectionSystem.ReportEngine.Domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.List;

import static hbm.fraudDetectionSystem.ReportEngine.Constant.ReportQueryConstant.*;

@Component
public class ReportRepository {

    protected final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    List<AlertStatisticByRule> fetchDataAlertStatisticByRule(Timestamp startDate, Timestamp endDate) {
        return jdbcTemplate.query(REPORT_ALERT_STATISTIC_BY_RULE_QUERY, (rs, rowNum) ->
                        new AlertStatisticByRule().builder()
                                .ruleId(rs.getLong("rule_id"))
                                .ruleName(rs.getString("rule_name"))
                                .totalAlerts(rs.getLong("alert_count"))
                                .build()
                , startDate, endDate);
    }

    List<AlertStatisticTotal> fetchDataAlertStatisticTotal(Timestamp startDate, Timestamp endDate) {
        return jdbcTemplate.query(REPORT_ALERT_STATISTIC_TOTAL_QUERY, (rs, rowNum) ->
                        new AlertStatisticTotal().builder()
                                .alertDate(rs.getTimestamp("alert_date"))
                                .totalDate(rs.getLong("total_alert"))
                                .build()
                , startDate, endDate);
    }

    List<UserActivityHistory> fetchDataUserActivityHistory(Timestamp startDate, Timestamp endDate) {
        return jdbcTemplate.query(REPORT_USER_ACTIVITY_HISTORY_QUERY, (rs, rowNum) ->
                        new UserActivityHistory().builder()
                                .username(rs.getString("username"))
                                .alertProcessedByUser(rs.getLong("alert_processed_by_user"))
                                .cardMarkedFraudulentByUser(rs.getLong("card_marked_fraudulent_by_user"))
                                .build()
                , startDate, endDate, startDate, endDate);
    }

    List<DeclineOnline> fetchDataDeclineOnline(Timestamp startDate, Timestamp endDate) {
        return jdbcTemplate.query(REPORT_DECLINE_ONLINE_QUERY, (rs, rowNum) ->
                        new DeclineOnline().builder()
                                .authDate(rs.getString("auth_date"))
                                .respCode(rs.getString("resp_code"))
                                .respCodeDesc(rs.getString("resp_code_desc"))
                                .currency(rs.getString("currency"))
                                .total(rs.getString("total"))
                                .build()
                , startDate, endDate);
    }

    List<CardFraudulentTerminal> fetchDataCardHadTransOnFraudulentTerminal(Timestamp startDate, Timestamp endDate) {
        return jdbcTemplate.query(REPORT_CARD_HAD_TRANS_ON_FRAUDULENT_TERMINAL_QUERY, (rs, rowNum) ->
                        new CardFraudulentTerminal().builder()
                                .hpan(rs.getString("hpan"))
                                .utrnno(rs.getLong("utrnno"))
                                .terminalId(rs.getString("terminal_id"))
                                .transDate(rs.getString("trans_date"))
                                .amount(rs.getString("amount"))
                                .respCode(rs.getString("resp_code"))
                                .currency(rs.getString("currency"))
                                .build()
                , startDate, endDate);
    }

    List<RuleEffectiveness> fetchDataRuleEffectiveness(Timestamp startDate, Timestamp endDate) {
        return jdbcTemplate.query(REPORT_RULE_EFFECTIVENESS_QUERY, (rs, rowNum) ->
                        new RuleEffectiveness().builder()
                                .ruleId(rs.getLong("rule_id"))
                                .ruleName(rs.getString("rule_name"))
                                .totalFraudTransTriggered(rs.getLong("total_fraud_trans_triggered"))
                                .build()
                , startDate, endDate);
    }

    List<CardAddedToStopList> fetchDataCardAddedToStopList(Timestamp startDate, Timestamp endDate) {
        return jdbcTemplate.query(REPORT_CARD_ADDED_TO_STOP_LIST_QUERY, (rs, rowNum) ->
                        new CardAddedToStopList().builder()
                                .value(rs.getString("value"))
                                .blockingTime(rs.getString("blocking_time"))
                                .userId(rs.getLong("user_id"))
                                .username(rs.getString("username"))
                                .totalAlertHandled(rs.getLong("total_alert_handled"))
                                .build()
                , startDate, endDate, startDate, endDate);
    }
}
