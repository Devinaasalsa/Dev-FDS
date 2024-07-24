package hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.ExReaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExReactionRepository extends JpaRepository<ReactionContainer, Long> {
    List<ReactionContainer> findAllByUtrnnoOrderByIdAsc(long utrnno);
}
