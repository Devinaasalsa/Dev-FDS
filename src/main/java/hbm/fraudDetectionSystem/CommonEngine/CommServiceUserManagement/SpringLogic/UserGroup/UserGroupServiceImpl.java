package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.constant.UserGroupConstant;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.UserGroupExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.UserGroupNotFoundException;
import org.apache.commons.lang3.StringUtils;
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

@Service
public class UserGroupServiceImpl implements UserGroupService {
    @PersistenceContext
    private EntityManager em;
    private final UserGroupRepository userGroupRepository;

    public UserGroupServiceImpl(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }

    @Override
    public UserGroup addUserGroup(String groupName) throws UserGroupNotFoundException, UserGroupExistException {
        validateNewUserGroup(StringUtils.EMPTY, groupName);
        UserGroup userGroup = new UserGroup();
        userGroup.setGroupName(groupName);
        userGroupRepository.save(userGroup);
        return userGroup;
    }

    @Override
    public List<UserGroup> getUserGroups() {
        return userGroupRepository.findByOrderByIdAsc();
    }

    @Override
    public UserGroup updateUserGroup(String currentGroupName, String newGroupName) throws UserGroupNotFoundException, UserGroupExistException {
        UserGroup currentUserGroup = validateNewUserGroup(currentGroupName, newGroupName);
        assert currentUserGroup != null;
        currentUserGroup.setGroupName(newGroupName);
        userGroupRepository.save(currentUserGroup);
        return currentUserGroup;
    }

    @Override
    public void deleteUserGroup(long id) {
        userGroupRepository.deleteById(id);
    }

    @Override
    public UserGroup findUserGroupByGroupName(String groupName) {
        return userGroupRepository.findUserGroupByGroupName(groupName);
    }

    @Override
    public UserGroup findUserGroupById(long id) {
        return userGroupRepository.findUserGroupById(id);
    }

    @Override
    public List<UserGroup> search(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<UserGroup> query = cb.createQuery(UserGroup.class);
        Root<UserGroup> root = query.from(UserGroup.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (!value.toString().isEmpty()) {
                        predicates.add(cb.equal(root.get(key), value));
                    }
                });

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<UserGroup> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    private UserGroup validateNewUserGroup(String currentGroupName, String newGroupName) throws UserGroupNotFoundException, UserGroupExistException {
        UserGroup userGroupByNewGroupName = findUserGroupByGroupName(newGroupName);

        if (StringUtils.isNotBlank(currentGroupName)) {
            UserGroup currentUserGroup = findUserGroupByGroupName(currentGroupName);
            if (currentUserGroup == null) {
                throw new UserGroupNotFoundException(UserGroupConstant.NO_USER_GROUP_FOUND_BY_GROUP_NAME + currentGroupName);
            }
            if (userGroupByNewGroupName != null && !currentUserGroup.getId().equals(userGroupByNewGroupName.getId())) {
                throw new UserGroupExistException(UserGroupConstant.USER_GROUP_ALREADY_EXISTS);
            }

            return currentUserGroup;
        } else {
            if (userGroupByNewGroupName != null) {
                throw new UserGroupExistException(UserGroupConstant.USER_GROUP_ALREADY_EXISTS);
            }

            return null;
        }
    }

}
