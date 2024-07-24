package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleHistory;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_RULE_HISTORY")
public class RuleHistory {
    @Id
    @SequenceGenerator(name = "rule_history_seq", sequenceName = "rule_history_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "rule_history_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long historyId;
    private String initiator;
    @Column(name = "\"timestamp\"", columnDefinition = "TIMESTAMP")
    private Timestamp timestamp;
    @Column(name = "\"comment\"")
    private String comment;
    private String description;
    private String status;
    private Long ruleId;
}
