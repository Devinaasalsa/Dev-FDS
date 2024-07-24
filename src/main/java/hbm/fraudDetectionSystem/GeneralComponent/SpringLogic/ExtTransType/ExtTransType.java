package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtTransType;

import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ChannelFormatter.ChannelFormatter;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.RespTab.RespTab;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransTypeTab.TransTypeTab;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "EXT_TRANS_TYPE")
public class ExtTransType {
    @Id
    @SequenceGenerator(name = "ext_trans_type_sequence", sequenceName = "ext_trans_type_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "ext_trans_type_sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String transType;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "int_trans_type",
            referencedColumnName = "id"
    )
    private TransTypeTab intTransType;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "config_id",
            referencedColumnName = "configId"
    )
    private MessageConfiguration configId;
}
