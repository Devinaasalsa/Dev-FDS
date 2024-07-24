package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleGroup;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroup;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroupRepository;
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
public class RuleGroupServiceImpl implements RuleGroupService {
    @PersistenceContext
    protected EntityManager em;
    protected final RuleGroupRepository ruleGroupRepository;
    protected final UserGroupRepository userGroupRepository;

    @Autowired
    public RuleGroupServiceImpl(RuleGroupRepository ruleGroupRepository, UserGroupRepository userGroupRepository) {
        this.ruleGroupRepository = ruleGroupRepository;
        this.userGroupRepository = userGroupRepository;
    }

    @Override
    public List<RuleGroup> listRulesGroup(long uGroupId) {
        return ruleGroupRepository.findByUserGroupIdOrderByIdAsc(uGroupId);
    }

    @Override
    public List<RuleGroup> fetchRuleGroupByUserGroup(long userGroupId) {
        return ruleGroupRepository.findAllByUserGroupIdOrderByPriorityAsc(userGroupId);
    }

    @Override
    public void addRuleGroup(RuleGroup ruleGroup) {
        validateRuleGroupName(ruleGroup.getGroupName(), 0L);
        validateUserGroup(ruleGroup.getUserGroup().getId());
        ruleGroupRepository.save(ruleGroup);
    }

    @Override
    public void updateRuleGroup(RuleGroup ruleGroup) {
        validateRuleGroupId(ruleGroup.getId());
        validateRuleGroupName(ruleGroup.getGroupName(), ruleGroup.getId());
        validateUserGroup(ruleGroup.getUserGroup().getId());
        ruleGroupRepository.save(ruleGroup);
    }

    @Override
    public void deleteRulesGroup(long id) {
        ruleGroupRepository.deleteById(id);
    }

    @Override
    public List<RuleGroup> searchRuleGroup(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<RuleGroup> query = cb.createQuery(RuleGroup.class);
        Root<RuleGroup> root = query.from(RuleGroup.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!value.toString().isEmpty()) {
                            if (key.equals("userGroup")) {
                                Long userGroupId = Long.parseLong(value.toString());
                                Join<RuleGroup, UserGroup> userGroupJoin = root.join("userGroup");
                                predicates.add(cb.equal(userGroupJoin.get("id"), userGroupId));
                            } else if (key.equals("groupName")) {
                                String likeValue = "%" + value + "%";
                                predicates.add(cb.like(root.get(key), likeValue));
                            } else {
                                predicates.add(cb.equal(root.get(key), value));
                            }
                        }
                });

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<RuleGroup> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    protected void validateUserGroup(long groupId) {
        userGroupRepository.findById(groupId).orElseThrow(() ->
                new RuntimeException("User Group Id not found!"));
    }

    protected void validateRuleGroupId(long id) {
        ruleGroupRepository.findById(id).orElseThrow(() -> new RuntimeException("Rule Group Id Not Found"));
    }

    protected void validateRuleGroupName(String name, long id) {
        ruleGroupRepository.findRuleGroupByGroupName(name).ifPresent(existingData -> {
            if (existingData.getId() != id) {
                throw new RuntimeException(
                        String.format(
                                "Name [%s] is already exist",
                                name
                        )
                );
            }
        });
    }
}
