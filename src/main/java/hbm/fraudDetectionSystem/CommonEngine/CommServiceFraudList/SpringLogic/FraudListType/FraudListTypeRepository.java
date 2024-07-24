package hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FraudListTypeRepository extends JpaRepository<FraudListType, Integer> {
    FraudListType findByTypeId(int id);
    List<FraudListType> findByOrderByTypeIdAsc();
}
