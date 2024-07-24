package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageType;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "message_type")
public class MessageType {
    @Id
    @SequenceGenerator(name = "message_type_sequence", sequenceName = "message_type_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "message_type_sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long msgId;
    @Column(nullable = false, unique = true)
    private String name;
}
