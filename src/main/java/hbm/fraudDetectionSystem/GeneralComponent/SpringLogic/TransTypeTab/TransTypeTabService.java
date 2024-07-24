package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransTypeTab;

import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.RespTab.RespTab;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TransTypeTabService {
    @PersistenceContext
    protected EntityManager em;
    protected final TransTypeTabRepository transTypeTabRepository;

    @Autowired
    public TransTypeTabService(TransTypeTabRepository transTypeTabRepository) {
        this.transTypeTabRepository = transTypeTabRepository;
    }

    public List<TransTypeTab> findAll() {
        return this.transTypeTabRepository.findAll();
    }

    public void add(TransTypeTab data) {
        this.validateCode(0L, data.getCode());
        this.transTypeTabRepository.save(data);
    }

    public void update(TransTypeTab data) {
        this.validateId(data.getId());
        this.validateCode(data.getId(), data.getCode());
        this.transTypeTabRepository.save(data);
    }

    public void delete(long id) {
        this.transTypeTabRepository.deleteById(id);
    }

    public List<TransTypeTab> search(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<TransTypeTab> query = cb.createQuery(TransTypeTab.class);
        Root<TransTypeTab> root = query.from(TransTypeTab.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!value.toString().isEmpty()) {
                            predicates.add(cb.equal(root.get(key), value));
                        }
                });

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<TransTypeTab> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    private void validateId(long id) {
        this.transTypeTabRepository.findById(id).orElseThrow(() -> new RuntimeException("Existing data not found"));
    }

    protected void validateCode(long id, String value) {
        this.transTypeTabRepository.findByCode(value)
                .ifPresent(v1 -> {
                    if (v1.getId() != id) {
                        throw new RuntimeException("Data already exist");
                    }
                });
    }
}
