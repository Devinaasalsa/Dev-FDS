package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransTypeDesc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransTypeDescRepository extends JpaRepository<TransTypeDesc, Long> {
    List<TransTypeDesc> findAllByOrderByTypeId();
}
