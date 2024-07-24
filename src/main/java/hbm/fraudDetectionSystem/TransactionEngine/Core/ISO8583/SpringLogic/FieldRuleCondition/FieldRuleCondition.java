package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@JsonIgnoreProperties(value = {"condition", "description"})
@Getter
@Setter
@Entity
@Table(name = "FIELD_RULE_CONDITION")
public class FieldRuleCondition {
    @Id
    @SequenceGenerator(name = "field_rule_Condition_Sequence",sequenceName = "field_rule_Condition_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "field_rule_Condition_Sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long condId;
    private String condition;
    private String description;
}
