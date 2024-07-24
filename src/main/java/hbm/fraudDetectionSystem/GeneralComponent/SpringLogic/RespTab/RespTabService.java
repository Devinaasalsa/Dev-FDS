package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.RespTab;

import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule.Rule;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleGroup.RuleGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class RespTabService {
    @PersistenceContext
    protected EntityManager em;
    protected final RespTabRepository respTabRepository;

    @Autowired
    public RespTabService(RespTabRepository respTabRepository) {
        this.respTabRepository = respTabRepository;
    }

    public List<RespTab> findAll() {
        return this.respTabRepository.findAll();
    }

    public void add(RespTab data) {
        this.validateCode(0L, data.getCode());
        this.respTabRepository.save(data);
    }

    public void update(RespTab data) {
        this.validateId(data.getId());
        this.validateCode(data.getId(), data.getCode());
        this.respTabRepository.save(data);
    }

    public void delete(long id) {
        this.respTabRepository.deleteById(id);
    }

    public List<RespTab> search(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<RespTab> query = cb.createQuery(RespTab.class);
        Root<RespTab> root = query.from(RespTab.class);

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

        TypedQuery<RespTab> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    private void validateId(long id) {
        this.respTabRepository.findById(id).orElseThrow(() -> new RuntimeException("Existing data not found"));
    }

    protected void validateCode(long id, String value) {
        this.respTabRepository.findByCode(value)
                .ifPresent(v1 -> {
                    if (v1.getId() != id) {
                        throw new RuntimeException("Data already exist");
                    }
                });
    }
}
