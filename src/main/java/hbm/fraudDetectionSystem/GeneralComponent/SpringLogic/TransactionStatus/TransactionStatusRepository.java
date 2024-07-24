package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransactionStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static hbm.fraudDetectionSystem.GeneralComponent.Constant.TransactionStatusQueryConstant.*;

@Component
public class TransactionStatusRepository {
    protected final JdbcTemplate jdbcTemplate;

    @Autowired
    public TransactionStatusRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    List<TransactionStatus> fetchDataDaily() {
        return jdbcTemplate.query(TRANSACTION_STATUS_DAILY_QUERY, (rs, rowNum) ->
                new TransactionStatus().builder()
                        .fraudCategory(rs.getString("fraud_category"))
                        .totalCount(rs.getInt("total_count")).build());
    }

    List<TransactionStatus> fetchDataWeekly() {
        return jdbcTemplate.query(TRANSACTION_STATUS_WEEKLY_QUERY, (rs, rowNum) ->
                new TransactionStatus().builder()
                        .fraudCategory(rs.getString("fraud_category"))
                        .totalCount(rs.getInt("total_count")).build());
    }

    List<TransactionStatus> fetchDataMonthly() {
        return jdbcTemplate.query(TRANSACTION_STATUS_MONTHLY_QUERY, (rs, rowNum) ->
                new TransactionStatus().builder()
                        .fraudCategory(rs.getString("fraud_category"))
                        .totalCount(rs.getInt("total_count")).build());
    }
}
