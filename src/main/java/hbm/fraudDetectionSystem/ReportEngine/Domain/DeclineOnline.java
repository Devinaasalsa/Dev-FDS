package hbm.fraudDetectionSystem.ReportEngine.Domain;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DeclineOnline {
    private String authDate;
    private String respCode;
    private String respCodeDesc;
    private String currency;
    private String total;
}
