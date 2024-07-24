package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint.ChannelEndpoint;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldValue.JSONFieldValue;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageType.MessageType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "MESSAGE_CONFIGURATION")
public class MessageConfiguration implements Serializable {
    @Id
    @SequenceGenerator(name = "msg_Config_Sequence", sequenceName = "msg_Config_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "msg_Config_Sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long configId;
    private String name;
    private String description;
    @Type(type = "yes_no")
    private boolean hasHeader;
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "msg_type",
            referencedColumnName = "msgId"
    )
    private MessageType msgType;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER)
    @OrderBy("endpointId ASC")
    @JoinColumn(name = "configId", referencedColumnName = "configId")
    private Set<ChannelEndpoint> endpoints = new HashSet<>();
}
