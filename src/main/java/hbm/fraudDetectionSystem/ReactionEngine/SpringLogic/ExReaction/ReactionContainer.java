package hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.ExReaction;

import com.google.gson.annotations.Expose;
import hbm.fraudDetectionSystem.ReactionEngine.Enum.ReactionEnum;
import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_EX_REACTIONS")
public class ReactionContainer {
    @Expose
    @Id
    @SequenceGenerator(name = "ex_reactions_seq", sequenceName = "ex_reactions_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "ex_reactions_seq")
    private Long id;
    @Expose
    private Long reactionId;
    @Expose
    private ReactionEnum reactionEnum;
    @Transient
    private Runnable reaction;
    @Expose
    private String reactionValue;
    @Expose
    private String bindingId;
    @Expose
    private String bindingType;
    @Expose
    private String zone;
    @Expose
    private Long utrnno;
}
