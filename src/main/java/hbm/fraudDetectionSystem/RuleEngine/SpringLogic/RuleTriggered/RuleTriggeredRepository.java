package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleTriggered;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleTriggeredRepository extends JpaRepository<RuleTriggered, Long> {
    List<RuleTriggered> findAllByUtrnnoOrderByIdAsc(long utrnno);
}
