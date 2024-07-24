package hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.SpringLogic.Case;


import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule.Rule;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_ALERTS_INVESTIGATION") //Before Cases Table
public class Case implements Serializable {
    @Id
    @SequenceGenerator(name = "alert_inv_seq", sequenceName = "alert_inv_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "alert_inv_seq")
    private Long caseId;
    private Long utrnno;
    private String hpan;
    private String initiator;
    private String clasifiedComment;
    private Timestamp alertDate;
    private Timestamp classifiedDate;
    private Timestamp actionDate;
    private Timestamp lastUpdate;
    private Integer clasificationType;
    private Integer actionType;
//    @Type(type = "yes_no")
    private Boolean isClassified;
//    @Type(type = "yes_no")
    private Boolean isActioned;
//    @Type(type = "yes_no")
    private Boolean isForwarded;
//    @ManyToOne(cascade = CascadeType.MERGE)
//    @JoinColumn(name = "forwardedTo",referencedColumnName = "id")
    private Long forwardedTo;
//    @Type(type = "yes_no")
    private Boolean isLocked;
    private String lockedBy;
    private String caseComment;

    //fraudList Configuration
    @Transient
    private String listId;
    @Transient
    private String value;

    //Blacklist and Whitelist Configuration
    @Transient
    private String reason;
    @Transient
    private String entityType;
    @Transient
    private String datein;
    @Transient
    private String dateout;
    @Transient
    private String userGroupId;
}
