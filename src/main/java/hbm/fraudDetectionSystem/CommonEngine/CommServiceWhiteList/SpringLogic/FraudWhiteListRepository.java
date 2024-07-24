package hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.SpringLogic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FraudWhiteListRepository extends JpaRepository<FraudWhiteList, Long> {
    List<FraudWhiteList> findAllByOrderByIdAsc();
    FraudWhiteList findAllById(long id);
    List<FraudWhiteList> findByOrderByIdAsc();
    Optional<FraudWhiteList> findByEntityTypeAndValue(String eType, String value);
}
