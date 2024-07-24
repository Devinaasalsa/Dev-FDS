package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleBody;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "T_RULE_BODY")
@IdClass(RuleBody.IdClass.class)
public class    RuleBody {
    @Id
    @SequenceGenerator(name = "rule_body_seq", sequenceName = "rule_body_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "rule_body_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Id
    private String conditionId;
    private String detailCondition;
    private String condition;
    private Long bindingId;
    @Data
    static class IdClass implements Serializable {
        private Long id;
        private String conditionId;
    }
}
