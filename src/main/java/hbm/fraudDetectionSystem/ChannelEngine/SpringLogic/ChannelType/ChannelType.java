package hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelType;

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
@Table(name = "CHANNEL_TYPE")
public class ChannelType implements Serializable {
    @Id
    @SequenceGenerator(
            name = "channel_type_sequence",
            sequenceName = "channel_type_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "channel_type_sequence"
    )
    private Long channelTypeId;
    private String value;
    private String description;
    //TODO : Integrate with Message Template
    //TODO : Can't Separate with ISOFieldConfiguration

}

