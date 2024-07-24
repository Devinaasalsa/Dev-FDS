package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldActionType;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "JSON_FIELD_ACTION_TYPE")
public class JSONFieldActionType {
    @Id
    @SequenceGenerator(name = "json_fActionType_Sequence", sequenceName = "json_fActionType_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "json_fActionType_Sequence")
    private Long typeId;

    private String description;

    private String expression;
}
