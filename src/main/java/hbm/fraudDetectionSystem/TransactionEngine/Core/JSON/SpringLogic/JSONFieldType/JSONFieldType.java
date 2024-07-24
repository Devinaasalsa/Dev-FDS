package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldType;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "JSON_FIELD_TYPE")
public class JSONFieldType {
    @Id
    @SequenceGenerator(name = "json_fType_Sequence", sequenceName = "json_fType_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "json_fType_Sequence")
    private Long typeId;
    private String description;
}
