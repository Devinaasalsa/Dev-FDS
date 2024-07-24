package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.AggregateCounters;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AggregateCounterRepository extends JpaRepository<AggregateCounter, Long> {
    AggregateCounter findCounterById(long id);

    List<AggregateCounter> findByOrderByIdAsc();
}
