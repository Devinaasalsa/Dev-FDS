package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserAudit;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroup;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.SpringLogic.FraudWhiteList;
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

@Service
@Transactional
public class UserAuditService {
    @PersistenceContext
    private EntityManager em;
    protected final UserAuditRepository userAuditRepository;

    @Autowired
    public UserAuditService(UserAuditRepository userAuditRepository) {
        this.userAuditRepository = userAuditRepository;
    }

    public List<UserAudit> fetchAllAudit() {
        return userAuditRepository.findByOrderByCaptureDateDesc();
    }

    public List<UserAudit> fetchAuditByUserId(Long id) {
        return userAuditRepository.findAllByUserId(id);
    }

    public void saveAuditLog(UserAudit userAudit) {
        userAuditRepository.save(userAudit);
    }

    public List<UserAudit> search(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<UserAudit> query = cb.createQuery(UserAudit.class);
        Root<UserAudit> root = query.from(UserAudit.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!key.equals("dateFrom") && !key.equals("dateTo"))
                            if (!value.toString().isEmpty()) {
                                if (key.equals("user")) {
                                    Long userId = Long.parseLong(value.toString());
                                    Join<FraudWhiteList, UserGroup> userJoin = root.join("user");
                                    predicates.add(cb.equal(userJoin.get("id"), userId));
                                } else {
                                    predicates.add(cb.equal(root.get(key), value));
                                }
                            }
                });

        String dateFrom = (String) reqBody.get("dateFrom");
        String dateTo = (String) reqBody.get("dateTo");

        if (dateFrom != null && dateTo != null) {
            if (!dateFrom.isEmpty() && !dateTo.isEmpty())
                predicates.add(cb.between(root.get("captureDate"), dateFrom, dateTo));
        } else {
            if (dateFrom != null) {
                if (!dateFrom.isEmpty())
                    predicates.add(cb.greaterThanOrEqualTo(root.get("captureDate"), dateFrom));
            }

            if (dateTo != null) {
                if (!dateTo.isEmpty())
                    predicates.add(cb.lessThanOrEqualTo(root.get("captureDate"), dateTo));
            }
        }

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<UserAudit> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }
}
