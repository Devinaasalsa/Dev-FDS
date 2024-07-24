package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TopRuleTriggered;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@Immutable
@Subselect(
        "select * from top_rule_triggered"
)
public class TopRuleTriggered {
    @Id
    @Column(name = "id")
    private Long id;

    private Long ruleId;

    private String ruleName;

    private Long totalTriggered;
}
