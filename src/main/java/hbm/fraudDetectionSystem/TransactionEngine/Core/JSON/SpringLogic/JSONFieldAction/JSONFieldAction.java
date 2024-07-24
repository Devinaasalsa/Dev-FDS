package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldAction;

import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldActionType.JSONFieldActionType;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldConfiguration.JSONFieldConfiguration;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "JSON_FIELD_ACTION")
public class JSONFieldAction {
    @Id
    @SequenceGenerator(name = "json_fAction_Sequence", sequenceName = "json_fAction_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "json_fAction_Sequence")
    private Long actionId;

    private Long fieldId;

    private Integer sequence;

    /*
        Args will use for supporting data for any action,
        this column can hold multiple data, seperated by ","
     */
    private String args;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "type",
            referencedColumnName = "typeId"
    )
    private JSONFieldActionType type;
}
