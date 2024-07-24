package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.TemplateSetup;

import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.NotificationType.NotificationType;
import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.NotificationType.NotificationTypeService;
import hbm.fraudDetectionSystem.NotificationEngine.exception.NotificationTypeNotFoundException;
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
public class TemplateSetupServiceImpl implements TemplateSetupService {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @PersistenceContext
    private EntityManager em;
    private TemplateSetupRepository repository;
    private NotificationTypeService notificationService;

    @Autowired
    public TemplateSetupServiceImpl(TemplateSetupRepository repository, NotificationTypeService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    @Override
    public List<TemplateSetup> findAllTemplateSetup() {
        return repository.findByOrderByTemplateIdAsc();
    }

    @Override
    public TemplateSetup addTemplateSetup(String templateText, String description, String subjectText, long notificationType) throws NotificationTypeNotFoundException {
        NotificationType getNotifType = validateNotificationType(notificationType);
        TemplateSetup templateSetup = new TemplateSetup();
        templateSetup.setTemplateText(templateText);
        templateSetup.setDescription(description);
        templateSetup.setSubjectText(subjectText);
        templateSetup.setNotificationType(getNotifType);
        repository.save(templateSetup);
        return templateSetup;
    }

    @Override
    public TemplateSetup updateTemplateSetup(long currentId, String newTemplateText, String newDescription, String subjectText, long newNotificationType) throws NotificationTypeNotFoundException {
        NotificationType notificationType = validateNotificationType(newNotificationType);
        TemplateSetup templateSetup = findTemplateSetupById(currentId);
        templateSetup.setTemplateText(newTemplateText);
        templateSetup.setDescription(newDescription);
        templateSetup.setSubjectText(subjectText);
        templateSetup.setNotificationType(notificationType);
        repository.save(templateSetup);
        return templateSetup;
    }

    @Override
    public void deleteTemplateId(long templateId) {
        TemplateSetup templateSetup = repository.findByTemplateId(templateId);
        repository.deleteById(templateSetup.getTemplateId());
    }

    @Override
    public TemplateSetup findByTemplateId(long templateId) {
        return repository.findByTemplateId(templateId);
    }

    @Override
    public List<TemplateSetup> search(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<TemplateSetup> query = cb.createQuery(TemplateSetup.class);
        Root<TemplateSetup> root = query.from(TemplateSetup.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!value.toString().isEmpty()) {
                            if (key.equals("notificationType")) {
                                long notificationTypeId = Long.parseLong(value.toString());
                                Join<TemplateSetup, NotificationType> notificationTypeJoin = root.join("notificationType");
                                predicates.add(cb.equal(notificationTypeJoin.get("id"), notificationTypeId));
                            } else {
                                predicates.add(cb.equal(root.get(key), value));
                            }
                        }
                });

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<TemplateSetup> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    private TemplateSetup findTemplateSetupById(long currentId) {
        return repository.findByTemplateId(currentId);
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
