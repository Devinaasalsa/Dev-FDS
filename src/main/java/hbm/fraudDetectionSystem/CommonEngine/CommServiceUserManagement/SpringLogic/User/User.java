package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Institution.Institution;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Role.Role;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroup;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserType.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Entity
//@JsonIgnoreProperties(value = {"id"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_USER_CONFIG")
public class User implements Serializable {
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "user_sequence"
    )
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    //@JsonIgnore
    private Long id;
    private String userId;
    private String firstName;
    private String lastName;
    private String username;
    @JsonProperty(access = WRITE_ONLY)
    private String password;
    private String email;
    private String profileImageUrl;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinDate;
    @Column(name = "is_active")
    @org.hibernate.annotations.Type(type = "yes_no")
    private boolean isActive;
    @Column(name = "is_not_locked")
    @org.hibernate.annotations.Type(type = "yes_no")
    private boolean isNotLocked;
    @Column(name = "reset_password")
    @org.hibernate.annotations.Type(type = "yes_no")
    private boolean resetPassword;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id")
    private Type type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_group_id")
    private UserGroup userGroup;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    public Role role;
}
