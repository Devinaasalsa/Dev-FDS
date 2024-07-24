package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.RecipientGroup;

import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.NotificationType.NotificationType;
import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.RecipientSetup.RecipientSetup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_RECIPIENT_GROUP")
public class RecipientGroup implements Serializable {
    @Id
    @SequenceGenerator(name = "recipient_group_seq", sequenceName = "recipient_group_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "recipient_group_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long groupId;
    @ManyToOne
    @JoinColumn(name = "notification_id", referencedColumnName = "id")
    private NotificationType notificationType;
    private String groupName;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", referencedColumnName = "groupId")
    private List<RecipientSetup> recipientSetups;

}
