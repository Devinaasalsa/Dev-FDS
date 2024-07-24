package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.SupportedMTI;

public class SupportedMTIQuery {
    public static final String INSERT_MTI_QUERY =
            "insert into supported_mti (id, value, is_response, is_reversal) " +
                    "values ((select " +
                    "case when (select (max(id)+1) from supported_mti) is null then 1 " +
                    "else (select (max(id)+1) from supported_mti) end as id), :value, :isResponse, :isReversal)";
}
