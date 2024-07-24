package hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudList;


import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListType.FraudListType;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListType.FraudListTypeService;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.constant.FraudListConstant;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.FraudListExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.ListFraudNameNotFound;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroup;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroupService;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans.CurrTrans;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FraudListServiceImpl implements FraudListService {

    @PersistenceContext
    private EntityManager em;
    private FraudListRepository repository;
    private UserGroupService userGroupService;
    private FraudListTypeService typeService;

    @Autowired
    public FraudListServiceImpl(FraudListRepository repository, UserGroupService userGroupService, FraudListTypeService typeService) {
        this.repository = repository;
        this.userGroupService = userGroupService;
        this.typeService = typeService;
    }

    @Override
    public List<FraudList> getSanctionList() {
        return repository.findByOrderByListIdAsc();
    }

    @Override
    public List<FraudList> getListByEntityType(int entityType) {
        return repository.findAllByEntityType_TypeId(entityType);
    }

    @Override
    public FraudList addList(FraudList newData) throws ListFraudNameNotFound, FraudListExistException {
        validateNewListName(StringUtils.EMPTY, newData.getListName());
        UserGroup getUserGroup = findUserGroup(newData.getUserGroup().getId());
        FraudList fraudList = new FraudList();

        if (newData.getEntityType() != null) {
            FraudListType entityType = typeService.findEntityTypeByTypeId(newData.getEntityType().getTypeId());
            fraudList.setEntityType(entityType);
        }

        fraudList.setListName(newData.getListName());
        fraudList.setUserGroup(getUserGroup);
        repository.save(fraudList);
        return fraudList;
    }

    @Override
    public FraudList updateList(String currentListName, FraudList newData) throws ListFraudNameNotFound, FraudListExistException {
        UserGroup getUserGroup = findUserGroup(newData.getUserGroup().getId());
        FraudList currentFraudList = validateNewListName(currentListName, newData.getListName());

        if (newData.getEntityType() != null) {
            FraudListType entityType = typeService.findEntityTypeByTypeId(newData.getEntityType().getTypeId());
            currentFraudList.setEntityType(entityType);
        }

        currentFraudList.setListName(newData.getListName());
        currentFraudList.setUserGroup(getUserGroup);
        repository.save(currentFraudList);
        return currentFraudList;
    }

    @Override
    public FraudList findListId(Long listId) {
        return repository.findAllByListId(listId);
    }

    @Override
    public void deleteSanctionList(Long listId) {
        FraudList fraudList = findListId(listId);
        repository.deleteById(fraudList.getListId());
    }

    @Override
    public List<FraudList> searchFraudList(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<FraudList> query = cb.createQuery(FraudList.class);
        Root<FraudList> root = query.from(FraudList.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!value.toString().isEmpty()) {
                            switch (key) {
                                case "userGroup":
                                    Long userGroupId = Long.parseLong(value.toString());
                                    Join<FraudList, UserGroup> userGroupJoin = root.join("userGroup");
                                    predicates.add(cb.equal(userGroupJoin.get("id"), userGroupId));
                                    break;

                                case "entityType":
                                    int entityTypeId = Integer.parseInt(value.toString());
                                    Join<FraudList, FraudListType> entityTypeJoin = root.join("entityType");
                                    predicates.add(cb.equal(entityTypeJoin.get("typeId"), entityTypeId));
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

        TypedQuery<FraudList> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    private UserGroup findUserGroup(Long userGroupId) {
        return userGroupService.findUserGroupById(userGroupId);
    }

    private FraudList validateNewListName(String currentListName, String newListName) throws ListFraudNameNotFound, FraudListExistException {
        FraudList listByNewListName = findByListName(newListName);

        if (StringUtils.isNotBlank(currentListName)) {
            FraudList currentList = findByListName(currentListName);
            if (currentList == null) {
                throw new ListFraudNameNotFound(FraudListConstant.LISTNAME_NOT_FOUND + currentListName);
            }
            if (listByNewListName != null && !currentList.getListId().equals(listByNewListName.getListId())) {
                throw new FraudListExistException(FraudListConstant.SANCTION_LIST_EXIST);
            }
            return currentList;
        } else {
            if (listByNewListName != null) {
                throw new FraudListExistException(FraudListConstant.SANCTION_LIST_EXIST);
            }
            return null;
        }
    }

    private FraudList findByListName(String newListName) {
        return repository.findSanctionListByListName(newListName);
    }


}
