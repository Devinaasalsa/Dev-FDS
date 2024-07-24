package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_NOTIFICATION_TYPE")
public class NotificationType implements Serializable {
    @Id
    @SequenceGenerator(name = "notification_type_seq", sequenceName = "notification_type_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "notification_type_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String notificationType; // SMS dan EMAIL
}
