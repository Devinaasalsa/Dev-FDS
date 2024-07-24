package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserAudit;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "t_user_audit")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserAudit {
    @Id
    @SequenceGenerator(
            name = "user_audit_sequence",
            sequenceName = "user_audit_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "user_audit_sequence"
    )
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long auditId;
    private String captureDate;
    private Long timestamp;
    private String timeTaken;
    private Integer status;
    private String method;
    private String uri;
    private String host;
    private String userAgent;
    private String remoteAddress;
    private String reqContentType;
    private String respContentType;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id"
    )
    private User user;

    public UserAudit(String captureDate, Long timestamp) {
        this.captureDate = captureDate;
        this.timestamp = timestamp;
    }
}
