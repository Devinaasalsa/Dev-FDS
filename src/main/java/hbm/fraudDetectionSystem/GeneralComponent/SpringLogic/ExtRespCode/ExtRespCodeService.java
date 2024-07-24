package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.ExtRespCode;

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
public class ExtRespCodeService {
    @PersistenceContext
    protected EntityManager em;
    protected final ExtRespCodeRepository extRespCodeRepository;

    @Autowired
    public ExtRespCodeService(ExtRespCodeRepository extRespCodeRepository) {
        this.extRespCodeRepository = extRespCodeRepository;
    }

    public List<ExtRespCode> fetchAllData() {
        return this.extRespCodeRepository.findAll();
    }

    public List<ExtRespCode> fetchAllByConfigId(long configId) {
        return this.extRespCodeRepository.findAllByConfigIdConfigId(configId);
    }

    public void add(ExtRespCode extRespcode) {
        this.validateData(extRespcode);
        this.extRespCodeRepository.save(extRespcode);
    }

    public void update(ExtRespCode extRespcode) {
        this.validateId(extRespcode.getId());
        this.validateData(extRespcode);
        this.extRespCodeRepository.save(extRespcode);
    }

    public void delete(long id) {
        this.extRespCodeRepository.deleteById(id);
    }

    public List<ExtRespCode> search(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<ExtRespCode> query = cb.createQuery(ExtRespCode.class);
        Root<ExtRespCode> root = query.from(ExtRespCode.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!value.toString().isEmpty()) {
                            switch (key) {
                                case "intResp":
                                    Long intRespId = Long.parseLong(value.toString());
                                    Join<ExtRespCode, RespTab> intRespJoin = root.join("intResp");
                                    predicates.add(cb.equal(intRespJoin.get("id"), intRespId));
                                    break;

                                case "configId":
                                    int configId = Integer.parseInt(value.toString());
                                    Join<ExtRespCode, MessageConfiguration> configJoin = root.join("configId");
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

        TypedQuery<ExtRespCode> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    protected void validateId(long id) {
        this.extRespCodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Id Not Found"));
    }

    protected void validateData(ExtRespCode extRespcode) {
        this.extRespCodeRepository
                .findByConfigIdConfigIdAndIntResp_IdAndRespCode(
                        extRespcode.getConfigId().getConfigId(),
                        extRespcode.getIntResp().getId(),
                        extRespcode.getRespCode()
                ).ifPresent(v1 -> {
                    if (!Objects.equals(v1.getId(), extRespcode.getId()))
                        throw new RuntimeException("Resp code already exist");
                });
    }
}
