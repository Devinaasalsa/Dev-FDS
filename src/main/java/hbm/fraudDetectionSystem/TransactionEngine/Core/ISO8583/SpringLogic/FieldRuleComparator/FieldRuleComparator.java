package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleComparator;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleValue.FieldRuleValue;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "field_rule_comparator")
public class FieldRuleComparator {
    @Id
    @SequenceGenerator(name = "field_rule_comparator_Sequence",sequenceName = "field_rule_comparator_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "field_rule_comparator_Sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long comparatorId;
    private String operator;
    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name ="value_id1",
            referencedColumnName = "valueId"
    )
    private FieldRuleValue valueId1;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name ="value_id2",
            referencedColumnName = "valueId"
    )
    private FieldRuleValue valueId2;
}
