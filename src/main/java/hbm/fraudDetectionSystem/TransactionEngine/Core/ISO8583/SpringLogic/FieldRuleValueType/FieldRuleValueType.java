package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleValueType;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "field_rule_value_type")
public class FieldRuleValueType {
    @Id
    @SequenceGenerator(name = "rule_Value_Type_Sequence",sequenceName = "rule_Value_Type_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "rule_Value_Type_Sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long typeId;
    private String valueType;
}
