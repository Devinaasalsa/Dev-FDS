package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.RecipientSetup;

import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.NotificationType.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_RECIPIENT_SETUP")
public class RecipientSetup implements Serializable {
    @Id
    @SequenceGenerator(name = "recipient_setup_seq", sequenceName = "recipient_setup_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "recipient_setup_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long recipientId;
    @ManyToOne
    @JoinColumn(name = "notification_id", referencedColumnName = "id")
    private NotificationType notificationType;
//    @ManyToOne
//    @JoinColumn(name = "group_id", referencedColumnName = "groupId")
//    private RecipientGroup groupId;

    private String firstName;
    private String lastName;
    private String contactValue;

    @Column(name = "group_id")
    private Long groupId;
}
