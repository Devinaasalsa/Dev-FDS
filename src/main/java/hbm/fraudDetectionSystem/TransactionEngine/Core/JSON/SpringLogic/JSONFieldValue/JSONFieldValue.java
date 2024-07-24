package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldValue;

import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldConfiguration.JSONFieldConfiguration;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "JSON_FIELD_VALUE")
public class JSONFieldValue {
    @Id
    @SequenceGenerator(name = "json_fValue_Sequence", sequenceName = "json_fValue_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "json_fValue_Sequence")
    private Long valueId;
    private String value;
    private String description;
    private Long fieldId;
}
