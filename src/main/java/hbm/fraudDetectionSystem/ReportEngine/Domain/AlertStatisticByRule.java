package hbm.fraudDetectionSystem.ReportEngine.Domain;

import lombok.*;

import java.math.BigInteger;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AlertStatisticByRule {
    private long ruleId;
    private String ruleName;
    private long totalAlerts;
}
