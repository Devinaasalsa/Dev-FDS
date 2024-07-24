package hbm.fraudDetectionSystem.ReactionEngine.Core.ReactionType;

import hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.FraudReaction.FraudReaction;

public class SetResponseCode {
    public String run(FraudReaction reaction) {
        return reaction.getActionValue();
    }
}
