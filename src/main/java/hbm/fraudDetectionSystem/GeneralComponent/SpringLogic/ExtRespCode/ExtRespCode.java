package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtRespCode;

import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ChannelFormatter.ChannelFormatter;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.RespTab.RespTab;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "EXT_RESP_TAB")
public class ExtRespCode {
    @Id
    @SequenceGenerator(name = "ext_resp_tab_sequence", sequenceName = "ext_resp_tab_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "ext_resp_tab_sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String respCode;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "int_resp",
            referencedColumnName = "id"
    )
    private RespTab intResp;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "config_id",
            referencedColumnName = "configId"
    )
    private MessageConfiguration configId;
}
