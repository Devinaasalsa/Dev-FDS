package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransMsgCfg;

import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransTypeTab.TransTypeTab;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.DerDesc.DerDesc;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransTypeDesc.TransTypeDesc;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransDataAttribute.TransDataAttribute;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "trans_msg_cfg")
public class TransMsgCfg {
    @Id
    @SequenceGenerator(name = "trans_msg_cfg_Sequence",sequenceName = "trans_msg_cfg_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "trans_msg_cfg_Sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long msgId;
    private String format;
    private String fld;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name ="config_id",
            referencedColumnName = "configId"
    )
    private MessageConfiguration configId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name ="der_id",
            referencedColumnName = "derId"
    )
    private DerDesc der;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(
//            name ="trans_type_id_temp",
//            referencedColumnName = "typeId"
//    )
//    private TransTypeDesc transTypeTemp;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name ="value_attr_id",
            referencedColumnName = "attrId"
    )
    private TransDataAttribute transAttr;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name ="trans_type_id",
            referencedColumnName = "id"
    )
    private TransTypeTab transType;
}
