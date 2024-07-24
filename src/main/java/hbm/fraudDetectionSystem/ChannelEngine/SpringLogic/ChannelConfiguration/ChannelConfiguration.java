package hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration;

import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint.ChannelEndpoint;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ChannelFormatter.ChannelFormatter;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ConnectionConfig.ConnectionConfig;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "CHANNEL_CONFIGURATION")
public class ChannelConfiguration implements Serializable {
    @Id
    @SequenceGenerator(
            name = "channel_configuration_sequence",
            sequenceName = "channel_configuration_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "channel_configuration_sequence"
    )
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    private String description;

    private String errorCode; //General Error Code

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "configId",
            referencedColumnName = "configId"
    )
    private MessageConfiguration msgConfig;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "fomatter",
            referencedColumnName = "formatId"
    )
    private ChannelFormatter formatter;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "connection_id", referencedColumnName = "pid")
    private ConnectionConfig connectionConfig;

    @Transient
    private ChannelEndpoint endpoint;
}
