package hbm.fraudDetectionSystem.ReportEngine.Domain;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserActivityHistory {
    private String username;
    private long alertProcessedByUser;
    private long cardMarkedFraudulentByUser;
}
