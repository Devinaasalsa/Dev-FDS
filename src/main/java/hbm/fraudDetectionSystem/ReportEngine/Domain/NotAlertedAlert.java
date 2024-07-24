package hbm.fraudDetectionSystem.ReportEngine.Domain;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotAlertedAlert {
    private long notAlerted;
    private long alerted;
}
