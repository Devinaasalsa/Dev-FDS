package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldConfiguration;

import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils.JSONFieldHeaderDictionary;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldAction.JSONFieldAction;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldValue.JSONFieldValue;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "JSON_FIELD_CONFIG")
public class JSONFieldConfiguration extends JSONFieldHeaderDictionary {
    @Id
    @SequenceGenerator(name = "json_fConfig_Sequence", sequenceName = "json_fConfig_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "json_fConfig_Sequence")
    private Long id;
    private Long parentField;
    private Integer level;
}
