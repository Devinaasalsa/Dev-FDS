package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.RecipientGroup;

import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.NotificationType.NotificationType;
import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.NotificationType.NotificationTypeService;
import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.RecipientSetup.RecipientSetup;
import hbm.fraudDetectionSystem.NotificationEngine.exception.NotificationTypeNotFoundException;
import hbm.fraudDetectionSystem.NotificationEngine.exception.RecipientSetupNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service

public class RecipientGroupServiceImpl implements RecipientGroupService {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @PersistenceContext
    private EntityManager em;
    private RecipientGroupRepository repository;
    private NotificationTypeService notificationService;

    @Autowired
    public RecipientGroupServiceImpl(RecipientGroupRepository repository, NotificationTypeService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    @Override
    public List<RecipientGroup> findAllRecipientGroup() {
        return repository.findByOrderByGroupIdAsc();
    }

    @Override
    public RecipientGroup addRecipientGroup(String groupName, long notificationType, List<RecipientSetup> recipientSetup) throws NotificationTypeNotFoundException, RecipientSetupNotFoundException {
        NotificationType getNotifType = validateNotificationType(notificationType);
        RecipientGroup recipientGroup = new RecipientGroup();
        recipientGroup.setGroupName(groupName);
        recipientGroup.setNotificationType(getNotifType);
        recipientGroup.setRecipientSetups(recipientSetup);
        repository.save(recipientGroup);
        return recipientGroup;
    }

    @Override
    public RecipientGroup updateRecipientGroup(long currentId, String newGroupName, long newNotificationType, List<RecipientSetup> newRecipientSetup) throws NotificationTypeNotFoundException, RecipientSetupNotFoundException {
        NotificationType notificationType = validateNotificationType(newNotificationType);
        RecipientGroup recipientGroup = findRecipientGroupById(currentId);
        recipientGroup.setGroupName(newGroupName);
        recipientGroup.setNotificationType(notificationType);
        recipientGroup.setRecipientSetups(newRecipientSetup);
        repository.save(recipientGroup);
        return recipientGroup;
    }

    @Override
    public void deleteGroupId(long groupId) {
        RecipientGroup recipientGroup = repository.findByGroupId(groupId);
        repository.deleteById(recipientGroup.getGroupId());
    }

    @Override
    public RecipientGroup findByGroupId(long groupId) {
        return repository.findByGroupId(groupId);
    }

    @Override
    public List<RecipientGroup> search(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<RecipientGroup> query = cb.createQuery(RecipientGroup.class);
        Root<RecipientGroup> root = query.from(RecipientGroup.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!value.toString().isEmpty()) {
                            if (key.equals("notificationType")) {
                                long notificationTypeId = Long.parseLong(value.toString());
                                Join<RecipientGroup, NotificationType> notificationTypeJoin = root.join("notificationType");
                                predicates.add(cb.equal(notificationTypeJoin.get("id"), notificationTypeId));
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

        TypedQuery<RecipientGroup> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    private RecipientGroup findRecipientGroupById(long currentId) {
        return repository.findByGroupId(currentId);
    }

    private NotificationType validateNotificationType(long id) throws NotificationTypeNotFoundException {
        NotificationType getNotificationType = notificationService.findNotificationTypeById(id);
        if (getNotificationType == null) {
            LOGGER.error("Notification Type Not Found by id: " + id);
            throw new NotificationTypeNotFoundException("Notification Type Not Found by id: " + id);
        }
        return getNotificationType;
    }
}
