package hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ConnectionConfig;

import hbm.fraudDetectionSystem.ChannelEngine.Constant.ChannelState;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint.ChannelEndpoint;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.TCPType.TCPType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor

@AllArgsConstructor
@Builder
@Table(name = "CHANNEL_CONNECTION")
//@IdClass(ConnectionConfigPK.class)
public class ConnectionConfig {
    @Id
    @SequenceGenerator(
            name = "connection_configuration_sequence",
            sequenceName = "connection_configuration_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "connection_configuration_sequence"
    )
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;
    private Integer remotePort;
    private Integer localPort;
    private String remoteAddr;
    @Type(type = "yes_no")
    private Boolean keepAlive;
    @Type(type = "yes_no")
    private Boolean logonReq;
    private ChannelState state;
    private Integer toTime;
    private String baseEndpoint;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "type_id",
            referencedColumnName = "tcpTypeId"
    )
    private TCPType type;
}
