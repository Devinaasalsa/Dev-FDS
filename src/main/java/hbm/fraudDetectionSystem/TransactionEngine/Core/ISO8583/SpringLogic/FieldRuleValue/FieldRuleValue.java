package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleValue;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleValueType.FieldRuleValueType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "field_RULE_VALUE")
public class FieldRuleValue {
    @Id
    @SequenceGenerator(name = "rule_value_Sequence",sequenceName = "rule_value_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "rule_value_Sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long valueId;
    private String value;
    private String description;

    @ManyToOne(
            cascade = CascadeType.PERSIST
    )
    @JoinColumn(
            name ="type_id",
            referencedColumnName = "typeId"
    )
    private FieldRuleValueType typeId;
}
