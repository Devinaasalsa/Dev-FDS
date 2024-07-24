package hbm.fraudDetectionSystem.ReportEngine.Domain;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CardFraudulentTerminal {
    private String hpan;
    private long utrnno;
    private String terminalId;
    private String transDate;
    private String amount;
    private String respCode;
    private String currency;
}
