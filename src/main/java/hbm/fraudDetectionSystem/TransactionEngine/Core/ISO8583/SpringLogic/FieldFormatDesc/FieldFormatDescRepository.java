package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldFormatDesc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldFormatDescRepository extends JpaRepository<FieldFormatDesc, Long> {
    List<FieldFormatDesc> findAllByOrderByFormatIdAsc();
}
