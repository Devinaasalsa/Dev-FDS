package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.TemplateSetup;

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
@Table(name = "T_TEMPLATE_SETUP")
public class TemplateSetup implements Serializable {
    @Id
    @SequenceGenerator(name = "template_setup_seq", sequenceName = "template_setup_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "template_setup_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long templateId;
    @ManyToOne
    @JoinColumn(name = "notification_id", referencedColumnName = "id")
    private NotificationType notificationType;
    private String subjectText;
    private String templateText;
    private String description;
}


