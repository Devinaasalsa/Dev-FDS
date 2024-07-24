package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.UserNotification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "T_USER_NOTIFICATION")
public class UserNotification implements Serializable {
    @Id
    @SequenceGenerator(name = "user_notif_seq", sequenceName = "user_notif_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "user_notif_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String initiator;
    private String notificationType;
    private String messageType;
    private String reicipientMessage;
    private Timestamp dateOccurance;
    private String description;
}
