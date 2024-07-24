package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule;

import hbm.fraudDetectionSystem.RuleEngine.Enum.RuleState;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleBody.RuleBody;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleGroup.RuleGroup;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleHistory.RuleHistory;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "T_RULE")
public class Rule implements Serializable {
    @Id
    @SequenceGenerator(name = "rule_seq", sequenceName = "rule_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "rule_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long ruleId;
    private String ruleName;
    private String description;
    @Type(type = "yes_no")
    private Boolean isActive;
    private Integer riskValue;
    private Timestamp dateFrom;
    private Timestamp dateTo;
    private Integer priority;
    private Integer type;
    private String author;
    private String sFormula;
    private String status;//Approved, Rejected, Waiting_Confirmation
    private RuleState state;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rule_group_id", referencedColumnName = "id")
    private RuleGroup ruleGroup;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "rule_body", referencedColumnName = "ruleId")
    private List<RuleBody> ruleBodies;

    @Transient
    @OneToMany(cascade = {CascadeType.REMOVE}, fetch = FetchType.EAGER, mappedBy = "ruleId")
    @OrderBy("timeStamp DESC")
    private Set<RuleHistory> ruleHistory;
}
