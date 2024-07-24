package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.HeaderConfiguration;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldEncodingDesc.FieldEncodingDesc;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldFormatDesc.FieldFormatDesc;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "header_configuration")
public class HeaderConfiguration {
    @Id
    @SequenceGenerator(name = "header_config_sequence", sequenceName = "header_config_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "header_config_sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Integer fieldId;
    private Integer length;
    private String description;
    private Integer priority = 1;
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name ="format_id",
            referencedColumnName = "formatId"
    )
    private FieldFormatDesc formatId;
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name ="encoding_id",
            referencedColumnName = "encodingId"
    )
    private FieldEncodingDesc encodingId;
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name ="cond_id",
            referencedColumnName = "condId"
    )
    private FieldRuleCondition condId;
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "config_id",
            referencedColumnName = "configId"
    )
    private MessageConfiguration configId;
}
