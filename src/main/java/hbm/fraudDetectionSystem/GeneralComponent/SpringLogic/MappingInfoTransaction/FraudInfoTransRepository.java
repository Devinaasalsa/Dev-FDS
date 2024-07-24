package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.MappingInfoTransaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudInfoTransRepository extends JpaRepository<FraudInfoTransaction, Long> {
    FraudInfoTransaction findByUtrnnoAndRefnum(String utrnno, String refnum);
}
