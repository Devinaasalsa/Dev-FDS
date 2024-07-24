package hbm.fraudDetectionSystem.GeneralComponent.Domain;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudList.FraudList;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.AggregateCounters.AggregateCounter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;



@Getter
@Setter
@Embeddable
public class RuleBindingId implements Serializable {
    @ManyToOne
    @JoinColumn(name = "list_id")
    private FraudList fraudList;
    @ManyToOne
    @JoinColumn(name = "counters_id")
    private AggregateCounter countersId;
}
