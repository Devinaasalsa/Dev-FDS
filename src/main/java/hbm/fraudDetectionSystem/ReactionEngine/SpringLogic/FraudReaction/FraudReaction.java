package hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.FraudReaction;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_FRAUD_REACTIONS")
//@IdClass(FraudReaction.IdClass.class)
public class FraudReaction {
    @Id
    @SequenceGenerator(name = "fraud_reactions_seq", sequenceName = "fraud_reactions_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "fraud_reactions_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Long bindingId;
    private String bindingType; //(whitelist, blacklist, rule & rule group)
    private int priority;
    private String zone;
    private String action;
    private String actionValue;
    private String description;

//    @Data
//    static class IdClass implements Serializable {
//        private Long id;
//        private Long bindingId;
//        private String action;
//    }
}
