package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.RecipientSetup;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudList.FraudList;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListValue.FraudValue;
import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.NotificationType.NotificationType;
import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.NotificationType.NotificationTypeService;
import hbm.fraudDetectionSystem.NotificationEngine.exception.NotificationTypeNotFoundException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.*;

@Service
public class RecipientSetupServiceImpl implements RecipientSetupService {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @PersistenceContext
    private EntityManager em;
    private RecipientSetupRepository repository;
    private NotificationTypeService notificationService;

    @Autowired
    public RecipientSetupServiceImpl(RecipientSetupRepository repository, NotificationTypeService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    @Override
    public List<RecipientSetup> findAllRecipientSetup() {
        return repository.findByOrderByRecipientIdAsc();
    }

    @Override
    public RecipientSetup addRecipientSetup(RecipientSetup setup) {
        this.validateEmail(0L, setup.getContactValue());
        return repository.save(setup);
    }

    @Override
    public RecipientSetup updateRecipientSetup(RecipientSetup setup) {
        this.validateEmail(setup.getRecipientId(), setup.getContactValue());
        return repository.save(setup);
    }

    @Override
    public void deleteRecipientId(long recipientId) {
        RecipientSetup recipientSetup = repository.findByRecipientId(recipientId);
        repository.deleteById(recipientSetup.getRecipientId());
    }

    @Override
    public RecipientSetup findByRecipientId(long recipientId) {
        return repository.findByRecipientId(recipientId);
    }

    @Override
    public List<RecipientSetup> search(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<RecipientSetup> query = cb.createQuery(RecipientSetup.class);
        Root<RecipientSetup> root = query.from(RecipientSetup.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!value.toString().isEmpty()) {
                            if (key.equals("notificationType")) {
                                long notificationTypeId = Long.parseLong(value.toString());
                                Join<RecipientSetup, NotificationType> notificationTypeJoin = root.join("notificationType");
                                predicates.add(cb.equal(notificationTypeJoin.get("id"), notificationTypeId));
                            } else if (key.equals("firstName")) {
                                String likeValue = "%" + value + "%";
                                predicates.add(cb.like(root.get(key), likeValue));
                            } else if (key.equals("lastName")) {
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

        TypedQuery<RecipientSetup> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    @Override
    public void addRecipientSetupToGroup(Map<String, Long> reqBody) throws NotificationTypeNotFoundException {
        long setupId = reqBody.get("setupId");
        long groupId = reqBody.get("groupId");

        this.repository.findById(setupId).ifPresent(data -> {
            data.setGroupId(groupId);
            this.repository.save(data);
        });
    }

    @Override
    public void removeRecipientSetupFromGroup(long id) throws NotificationTypeNotFoundException {
        this.repository.findById(id).ifPresent(data -> {
            data.setGroupId(null);
            this.repository.save(data);
        });
    }

    @Override
    public List<RecipientSetup> findAllByGroupId(long groupId) {
        return this.repository.findAllByGroupIdOrderByRecipientIdAsc(groupId);
    }

    @Override
    public void importContact(MultipartFile file) throws IOException {
        if (this.isValidExcelFile(file)) {
            LOGGER.info(
                    String.format(
                            "Processing file with name: [%s]",
                            file.getOriginalFilename()
                    )
            );

            List<RecipientSetup> contacts = this.extractDataFromFile(file.getInputStream());

            LOGGER.info(
                    String.format(
                            "Successfully insert data with total: [%s]",
                            repository.saveAll(contacts).size()
                    )
            );
        } else {
            throw new RuntimeException("The file isn't valid excel");
        }
    }

    private RecipientSetup findRecipientSetupById(long currentId) {
        return repository.findByRecipientId(currentId);
    }

    private NotificationType validateNotificationType(long id) throws NotificationTypeNotFoundException {
        NotificationType getNotificationType = notificationService.findNotificationTypeById(id);
        if (getNotificationType == null) {
            LOGGER.error("Notification Type Not Found by id: " + id);
            throw new NotificationTypeNotFoundException("Notification Type Not Found by id: " + id);
        }
        return getNotificationType;
    }

    protected void validateEmail(long setupId, String email) {
        repository.findByRecipientIdAndContactValue(setupId, email)
                .ifPresent(v1 -> {
                    if (v1.getRecipientId() != setupId) {
                        throw new RuntimeException("Email already exist");
                    }
                });
    }

    public boolean isValidExcelFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public List<RecipientSetup> extractDataFromFile(InputStream inputStream) {
        List<RecipientSetup> contacts = new ArrayList<>();

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("Contact");

            if (sheet == null) {
                LOGGER.error("Sheet not valid");
                throw new RuntimeException("Sheet not valid");
            }

            int rowIndex = 0;
            for (Row row : sheet) {

                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }

                Iterator<Cell> cellIterator = row.iterator();
                int cellIndex = 0;
                RecipientSetup contact = new RecipientSetup();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    switch (cellIndex) {
                        case 0:
                            contact.setNotificationType(this.validateNotificationType(this.getValue(cell)));
                            break;

                        case 1:
                            contact.setFirstName(cell.getStringCellValue());
                            break;

                        case 2:
                            contact.setLastName(cell.getStringCellValue());
                            break;

                        case 3:
                            contact.setContactValue(cell.getStringCellValue());
                            break;

                        default: break;
                    }
                    cellIndex++;
                }

                contacts.add(contact);
            }
        } catch (IOException | NotificationTypeNotFoundException e) {
            e.getStackTrace();
        }
        return contacts;
    }

    protected Long getValue(Cell cell) {
        DataFormatter fmt = new DataFormatter();

        return Long.parseLong(fmt.formatCellValue(cell).isEmpty() ? "" : fmt.formatCellValue(cell));
    }
}
