package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldActionType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JSONFieldActionTypeRepository extends JpaRepository<JSONFieldActionType, Long> {
    List<JSONFieldActionType> findAllByOrderByDescriptionAsc();
}
