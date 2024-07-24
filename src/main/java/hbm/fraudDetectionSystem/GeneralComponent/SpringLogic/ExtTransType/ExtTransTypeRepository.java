package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtTransType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExtTransTypeRepository extends JpaRepository<ExtTransType, Long> {
    List<ExtTransType> findAllByConfigIdConfigId(long configId);

    Optional<ExtTransType> findByConfigIdConfigIdAndIntTransType_IdAndTransType(long configId, long intTransType, String transType);
}
