package hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.FraudReaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class FraudReactionServiceImpl implements FraudReactionService {
    private final FraudReactionRepository repository;
    @PersistenceContext
    protected EntityManager em;

    @Autowired
    public FraudReactionServiceImpl(FraudReactionRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<FraudReaction> listFraudReactions() {
        return repository.findByOrderByIdAsc();
    }

    @Override
    public void add(FraudReaction reaction) {
        if (!Objects.equals(reaction.getBindingType(), "GROUP"))
            reaction.setZone("");
        repository.save(reaction);
    }

    @Override
    public void update(FraudReaction reaction) {
        validateReactionId(reaction.getId());
        if (!Objects.equals(reaction.getBindingType(), "GROUP"))
            reaction.setZone("");
        repository.save(reaction);
    }

    @Override
    public List<FraudReaction> findReactionByBindingTypeAndBindingId(String bindingType, long bindingId) {
        List<FraudReaction> getAll = new ArrayList<>();
        if (bindingType.equals("GROUP")) {
            getAll = repository.findByBindingTypeAndBindingIdOrderById(bindingType, bindingId);
        }
        return getAll;
    }

    @Override
    public void deleteFraudReactions(long id) {
        repository.deleteById(id);
    }

    @Override
    public List<FraudReaction> searchReaction(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<FraudReaction> query = cb.createQuery(FraudReaction.class);
        Root<FraudReaction> root = query.from(FraudReaction.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!value.toString().isEmpty())
                            predicates.add(cb.equal(root.get(key), value));
                });

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<FraudReaction> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    protected void validateReactionId(long id) {
        repository.findById(id).orElseThrow(() -> new RuntimeException("ReactionId Not Found"));
    }
}
