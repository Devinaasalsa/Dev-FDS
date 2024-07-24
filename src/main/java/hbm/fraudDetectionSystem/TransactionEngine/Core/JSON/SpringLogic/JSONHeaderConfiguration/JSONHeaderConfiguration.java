package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONHeaderConfiguration;

import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils.JSONFieldHeaderDictionary;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldAction.JSONFieldAction;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldConfiguration.JSONFieldConfiguration;
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
@Table(name = "JSON_HEADER_CONFIG")
@AssociationOverrides(
        {
                @AssociationOverride(
                        name = "validValues", joinColumns = @JoinColumn(name = "headerId", referencedColumnName = "id")
                ),
                @AssociationOverride(
                        name = "actions", joinColumns = @JoinColumn(name = "headerId", referencedColumnName = "id")
                ),
        }
)
public class JSONHeaderConfiguration extends JSONFieldHeaderDictionary {
    @Id
    @SequenceGenerator(name = "json_hConfig_Sequence", sequenceName = "json_hConfig_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "json_hConfig_Sequence")
    private Long id;

}
