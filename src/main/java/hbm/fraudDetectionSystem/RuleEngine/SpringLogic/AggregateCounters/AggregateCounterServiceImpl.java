package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.AggregateCounters;

import hbm.fraudDetectionSystem.GeneralComponent.Utility.DateHelper;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule.Rule;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleGroup.RuleGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AggregateCounterServiceImpl implements AggregateCounterService {
    @PersistenceContext
    protected EntityManager em;

    private AggregateCounterRepository repository;

    @Autowired
    public AggregateCounterServiceImpl(AggregateCounterRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<AggregateCounter> getAllAggregateCounters() {
        return repository.findByOrderByIdAsc();
    }

    @Override
    public AggregateCounter findById(long id) {
        return repository.findCounterById(id);
    }

    @Override
    public AggregateCounter add(AggregateCounter counter) {
        counter.setLastUpdate(new Timestamp(System.currentTimeMillis()));
        return repository.save(counter);
    }

    @Override
    public void update(AggregateCounter counter) {
        validateCounterId(counter.getId());
        counter.setLastUpdate(new Timestamp(System.currentTimeMillis()));
        repository.save(counter);
    }


    @Override
    public void delete(long id) {
        AggregateCounter getCounterId = repository.findCounterById(id);
        repository.deleteById(getCounterId.getId());
    }

    @Override
    public List<AggregateCounter> search(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<AggregateCounter> query = cb.createQuery(AggregateCounter.class);
        Root<AggregateCounter> root = query.from(AggregateCounter.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!value.toString().isEmpty()) {
                            String likeValue = "%" + value + "%";
                            predicates.add(cb.like(root.get(key), likeValue));
                        }
                });


        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        Order order = cb.asc(root.get("id"));
        query.orderBy(order);

        TypedQuery<AggregateCounter> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    protected void validateCounterId(long id) {
        repository.findById(id).orElseThrow(() -> new RuntimeException("Counter Id not found"));
    }
}
