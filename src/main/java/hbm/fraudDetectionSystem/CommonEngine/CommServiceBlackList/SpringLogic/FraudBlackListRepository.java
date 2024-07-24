package hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.SpringLogic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FraudBlackListRepository extends JpaRepository<FraudBlackList, Long> {
    List<FraudBlackList> findAllByOrderByIdAsc();
    FraudBlackList findAllById(long id);
    List<FraudBlackList> findByOrderByIdAsc();
    Optional<FraudBlackList> findByEntityTypeAndValue(String eType, String value);
}
