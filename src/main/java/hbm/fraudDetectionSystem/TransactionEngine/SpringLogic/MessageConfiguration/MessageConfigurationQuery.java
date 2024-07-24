package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration;

public class MessageConfigurationQuery {
    public static final String INSERT_ISO_CONFIG_QUERY =
            "insert into t_message_configuration (config_id, description, has_header, name) " +
                    "values ((select " +
                    "case when (select (max(config_id)+1) from t_message_configuration) is null then 1 " +
                    "else (select (max(config_id)+1) from t_message_configuration) end as config_id), :description, :hasHeader, :name)";
}
