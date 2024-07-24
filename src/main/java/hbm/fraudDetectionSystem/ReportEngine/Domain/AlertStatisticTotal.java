package hbm.fraudDetectionSystem.ReportEngine.Domain;

import lombok.*;

import java.math.BigInteger;
import java.sql.Timestamp;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AlertStatisticTotal {
    private Timestamp alertDate;
    private long totalDate;
}
