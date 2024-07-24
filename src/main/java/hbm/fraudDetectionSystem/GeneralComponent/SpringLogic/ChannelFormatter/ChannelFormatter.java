package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ChannelFormatter;


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
@Table(name = "CHANNEL_FORMATTER")
public class ChannelFormatter {
    @Id
    @SequenceGenerator(
            name = "channel_formatter_sequence",
            sequenceName = "channel_formatter_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "channel_formatter_sequence"
    )
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long formatId;

    private String description;
}
