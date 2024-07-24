package hbm.fraudDetectionSystem.ReportEngine.Domain;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RuleEffectiveness {
    private long ruleId;
    private String ruleName;
    private long totalFraudTransTriggered;
}
