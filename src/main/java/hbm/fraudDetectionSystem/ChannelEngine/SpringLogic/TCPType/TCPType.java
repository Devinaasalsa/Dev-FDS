package hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.TCPType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "TCP_TYPE")
public class TCPType {
    @Id
    @SequenceGenerator(
            name = "tcp_type_sequence",
            sequenceName = "tcp_type_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "tcp_type_sequence"
    )
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long tcpTypeId;
    String description;
}


