package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldConfiguration;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldEncodingDesc.FieldEncodingDesc;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldFormatDesc.FieldFormatDesc;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.SubFieldConfiguration.SubFieldConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "FIELD_CONFIGURATION")
public class FieldConfiguration implements Serializable {
    @Id
    @SequenceGenerator(name = "Field_Config_Sequence",sequenceName = "Field_Config_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "Field_Config_Sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name ="format_id",
            referencedColumnName = "formatId"
    )
    private FieldFormatDesc formatId;
    private Integer fieldId;
    private Integer length;
    private String description;
    @Type(type = "yes_no")
    private Boolean hasChild = false;
    @Type(type = "yes_no")
    private Boolean pad = false;
    private Integer priority = 1;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name ="encoding_id",
            referencedColumnName = "encodingId"
    )
    private FieldEncodingDesc encodingId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name ="config_id",
            referencedColumnName = "configId"
    )
    private MessageConfiguration configId;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name ="cond_id",
            referencedColumnName = "condId"
    )
    private FieldRuleCondition condId;

    @OneToMany(
            mappedBy = "parentId",
            fetch = FetchType.EAGER
    )
//    orphanRemoval = true
//    , cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}
    private List<SubFieldConfiguration> subFieldConfigurations = new ArrayList<>();

    public FieldConfiguration() {
    }
}
