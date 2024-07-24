package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleGroup;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroup;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_RULE_GROUP")
public class RuleGroup implements Serializable {
    @Id
    @SequenceGenerator(name = "rule_group_seq", sequenceName = "rule_group_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "rule_group_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String groupName;
    private int threshouldBlack;
    private int threshouldGrey;
    private int priority;
    @Type(type = "yes_no")
    private Boolean isActive;
    @Type(type = "yes_no")
    private Boolean isForcedReaction;
    @ManyToOne
    @JoinColumn(name = "user_group_id", referencedColumnName = "id")
    private UserGroup userGroup;
}
