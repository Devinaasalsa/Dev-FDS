package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldFormatter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JSONFieldFormatterRepository extends JpaRepository<JSONFieldFormatter, Long> {
    List<JSONFieldFormatter> findAllByOrderByFormatterIdAsc();
}
