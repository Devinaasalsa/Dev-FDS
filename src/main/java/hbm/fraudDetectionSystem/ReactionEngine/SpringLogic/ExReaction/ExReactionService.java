package hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.ExReaction;

import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleTriggered.RuleTriggered;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ExReactionService {
    protected final ExReactionRepository exReactionRepository;

    @Autowired
    public ExReactionService(ExReactionRepository exReactionRepository) {
        this.exReactionRepository = exReactionRepository;
    }

    public List<ReactionContainer> fetchExReactionsByUtrnno(long utrnno) {
        return this.exReactionRepository.findAllByUtrnnoOrderByIdAsc(utrnno);
    }
}
