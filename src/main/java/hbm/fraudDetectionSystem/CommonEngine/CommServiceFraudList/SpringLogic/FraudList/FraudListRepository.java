package hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudListRepository extends JpaRepository<FraudList, Long> {

    List<FraudList>findAllByEntityType_TypeId(int entityType);

    FraudList findSanctionListByListName(String listName);

    FraudList findAllByListId(long id);

    List<FraudList> findByOrderByListIdAsc();
}
