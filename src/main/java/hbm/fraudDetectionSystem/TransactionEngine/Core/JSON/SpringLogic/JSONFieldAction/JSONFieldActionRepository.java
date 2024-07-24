package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldAction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JSONFieldActionRepository extends JpaRepository<JSONFieldAction, Long> {
}
