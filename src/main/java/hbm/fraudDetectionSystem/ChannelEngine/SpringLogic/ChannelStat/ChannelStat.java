package hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelStat;

import hbm.fraudDetectionSystem.ChannelEngine.Constant.ChannelStatus;
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
@Table(name = "CHANNEL_STAT")
public class ChannelStat {
    @Id
    @SequenceGenerator(
            name = "channel_stat_sequence",
            sequenceName = "channel_stat_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "channel_stat_sequence"
    )
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;
    private ChannelStatus opStat;
}
