package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtTransType;

import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtRespCode.ExtRespCode;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.RespTab.RespTab;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class ExtTransTypeService {
    @PersistenceContext
    protected EntityManager em;
    protected final ExtTransTypeRepository extTransTypeRepository;

    @Autowired
    public ExtTransTypeService(ExtTransTypeRepository extTransTypeRepository) {
        this.extTransTypeRepository = extTransTypeRepository;
    }

    public List<ExtTransType> fetchAllData() {
        return this.extTransTypeRepository.findAll();
    }

    public List<ExtTransType> fetchAllByConfigId(long configId) {
        return this.extTransTypeRepository.findAllByConfigIdConfigId(configId);
    }

    public void add(ExtTransType extTransType) {
        this.validateData(extTransType);
        this.extTransTypeRepository.save(extTransType);
    }

    public void update(ExtTransType extTransType) {
        this.validateId(extTransType.getId());
        this.validateData(extTransType);
        this.extTransTypeRepository.save(extTransType);
    }

    public void delete(long id) {
        this.extTransTypeRepository.deleteById(id);
    }

    public List<ExtTransType> search(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<ExtTransType> query = cb.createQuery(ExtTransType.class);
        Root<ExtTransType> root = query.from(ExtTransType.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!value.toString().isEmpty()) {
                            switch (key) {
                                case "intTransType":
                                    Long intRespId = Long.parseLong(value.toString());
                                    Join<ExtTransType, RespTab> intRespJoin = root.join("intTransType");
                                    predicates.add(cb.equal(intRespJoin.get("id"), intRespId));
                                    break;

                                case "configId":
                                    int configId = Integer.parseInt(value.toString());
                                    Join<ExtTransType, MessageConfiguration> configJoin = root.join("configId");
                                    predicates.add(cb.equal(configJoin.get("configId"), configId));
                                    break;

                                default:
                                    predicates.add(cb.equal(root.get(key), value));
                                    break;
                            }
                        }
                });

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<ExtTransType> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    protected void validateId(long id) {
        this.extTransTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Id Not Found"));
    }

    protected void validateData(ExtTransType extTransType) {
        this.extTransTypeRepository
                .findByConfigIdConfigIdAndIntTransType_IdAndTransType(
                        extTransType.getConfigId().getConfigId(),
                        extTransType.getIntTransType().getId(),
                        extTransType.getTransType()
                ).ifPresent(v1 -> {
                    if (!Objects.equals(v1.getId(), extTransType.getId()))
                        throw new RuntimeException("Trans Type already exist");
                });
    }
}
