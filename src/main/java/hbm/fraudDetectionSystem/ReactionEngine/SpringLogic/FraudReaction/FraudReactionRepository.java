package hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.FraudReaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudReactionRepository extends JpaRepository<FraudReaction, Long> {
    List<FraudReaction> findByBindingTypeAndBindingIdOrderById(String bindingType, long bindingId);

    List<FraudReaction>findAllByBindingIdAndZone(long bindingId, String fraudZone);

//    List<FraudReaction> findByOrderByBindingTypeAscBindingIdAscPriorityAsc();
    List<FraudReaction> findByOrderByIdAsc();

    @Query(
            value = "SELECT *\n" +
                    "FROM t_fraud_reactions tfr\n" +
                    "WHERE tfr.binding_id = :bindingId\n" +
                    "  AND tfr.binding_type = :bindingType\n" +
                    "  AND tfr.zone = :zone",
            nativeQuery = true
    )
    List<FraudReaction> findReactionByBindingTypeAndBindingIdAndZone(String bindingType, long bindingId, String zone);

    void deleteAllByBindingTypeAndBindingId(String bindingType, long bindingId);
}
