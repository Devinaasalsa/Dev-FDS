package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleTriggered;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_RULE_TRIGGERED")
public class RuleTriggered implements Serializable {
    @Id
    @SequenceGenerator(name = "rule_triggered_seq", sequenceName = "rule_triggered_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "rule_triggered_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Long utrnno;
    @Column(length = 65535)
    private String detailObj;
    private Long ruleId;
}
