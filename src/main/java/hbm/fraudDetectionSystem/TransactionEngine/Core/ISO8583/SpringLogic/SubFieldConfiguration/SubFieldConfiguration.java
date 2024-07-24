package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.SubFieldConfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldConfiguration.FieldConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldEncodingDesc.FieldEncodingDesc;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldFormatDesc.FieldFormatDesc;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.SpringLogicHelper.convertNullInt2Zero;
import static hbm.fraudDetectionSystem.GeneralComponent.Utility.SpringLogicHelper.convertNullLong2Zero;

@Getter
@Setter
@Entity
@Table(name = "SUBFIELD_CONFIGURATION")
public class SubFieldConfiguration {
    @Id
    @SequenceGenerator(name = "SubField_Config_sequence", sequenceName = "SubField_Config_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SubField_Config_sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private int fieldId;
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name ="format_id",
            referencedColumnName = "formatId"
    )
    private FieldFormatDesc formatId;
    private int length;
    private String description;
//    @Type(type = "yes_no")
    private Boolean isTlvFormat;
    private Integer priority = 1;
    @Type(type = "yes_no")
    private Boolean pad = false;
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name ="encoding_id",
            referencedColumnName = "encodingId"
    )
    private FieldEncodingDesc encodingId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(
            name = "parent_id",
            referencedColumnName = "id"
    )
    private FieldConfiguration parentId;
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name ="cond_id",
            referencedColumnName = "condId"
    )
    private FieldRuleCondition condId;

    public SubFieldConfiguration() {
    }

    public void setParentId(FieldConfiguration parentId) {
        this.parentId = parentId;
    }

    public String uniqueAttributes() {
        return String.valueOf(getFieldId() + convertNullLong2Zero(getParentId().getId()) + convertNullInt2Zero(getPriority()));
    }
}
