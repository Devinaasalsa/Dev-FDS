package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransTypeTab;

import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelType.ChannelType;
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
@Table(name = "TRANS_TYPE_TAB")
public class TransTypeTab {
    @Id
    @SequenceGenerator(name = "trans_type_tab_sequence", sequenceName = "trans_type_tab_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "trans_type_tab_sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String code;
    private String description;
    @ManyToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "channelTypeId", referencedColumnName = "channelTypeId"
    )
    private ChannelType channelType;
}
