package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.HeaderConfiguration;

public class HeaderConfigurationQuery {
    public static final String INSERT_ISO_HEADER_QUERY =
            "insert into header_configuration (id, field_id, length, description, priority, format_id, encoding_id, cond_id, config_id) " +
            "values ((select case " +
                    "when (select (max(id)+1) from header_configuration) is null then 1 " +
                    "else (select (max(id)+1) from header_configuration) " +
                    "end as id), :headerField, :headerLength, :description, :priority, :headerFormat, :encoding, null, :configId)";
}
