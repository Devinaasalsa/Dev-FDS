package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JSONFieldTypeRepository extends JpaRepository<JSONFieldType, Long> {
    List<JSONFieldType> findAllByOrderByDescriptionAsc();
}
