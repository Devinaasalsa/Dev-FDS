package hbm.fraudDetectionSystem.ReportEngine.Domain;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CardAddedToStopList {
    private String value;
    private String blockingTime;
    private long userId;
    private String username;
    private long totalAlertHandled;
}
