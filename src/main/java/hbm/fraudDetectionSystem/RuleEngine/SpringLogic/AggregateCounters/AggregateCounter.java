package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.AggregateCounters;

import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Filtration.Filtration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.core.annotation.Order;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "T_AGGREGATE_COUNTER")
public class AggregateCounter implements Serializable {
    @Id
    @SequenceGenerator(name = "aggregate_counters_seq", sequenceName = "aggregate_counters_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "aggregate_counters_seq")
    private Long id;
    private String name;
    private Integer cycleType; //slidingShow||Fixed
    private String aggregatingEntity;
    private String attribute;
    private Integer metricType; //countMatched(1) || countDifferent(2)
    private Integer incrementationMode; //OriginalTransaction(1) || AdviceTransaction(2)
    private Long timeStartCycle;
    private Long timeEndCycle;
    private String description;
    @Type(type = "yes_no")
    private Boolean isFormulaEnabled;
    private String formula;
    private Timestamp lastUpdate;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "filtration", referencedColumnName = "id")
    @OrderBy("conditionId ASC")
    private List<Filtration> filtrations;
}
