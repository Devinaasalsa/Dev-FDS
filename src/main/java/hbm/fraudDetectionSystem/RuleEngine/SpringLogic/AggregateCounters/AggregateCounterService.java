package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.AggregateCounters;

import java.util.List;
import java.util.Map;

public interface AggregateCounterService {
    List<AggregateCounter> getAllAggregateCounters();

    AggregateCounter findById(long id);

    AggregateCounter add(AggregateCounter counter);

    void update(AggregateCounter counter);

    void delete(long id);

    List<AggregateCounter> search(Map<String, Object> reqBody);
}
