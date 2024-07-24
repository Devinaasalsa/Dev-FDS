package hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.FraudReaction;


import java.util.List;
import java.util.Map;

public interface FraudReactionService {

    List<FraudReaction> listFraudReactions();

    List<FraudReaction> findReactionByBindingTypeAndBindingId(String bindingType, long bindingId);

    void add(FraudReaction reaction);

    void update(FraudReaction reaction);

    void deleteFraudReactions(long id);

    List<FraudReaction> searchReaction(Map<String, Object> reqBody);
}


