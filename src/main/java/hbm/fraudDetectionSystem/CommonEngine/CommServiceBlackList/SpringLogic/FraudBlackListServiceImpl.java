package hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.SpringLogic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.constant.BlackListConstant;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.exception.BlackListExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.exception.BlackListNotFoundException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.User;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.UserService;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroup;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroupService;
import hbm.fraudDetectionSystem.ReactionEngine.Core.ReactionEngine;
import hbm.fraudDetectionSystem.ReactionEngine.Core.ReactionType.SetResponseCode;
import hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.ExReaction.ReactionContainer;
import hbm.fraudDetectionSystem.ReactionEngine.Enum.BindingTypeEnum;
import hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.FraudReaction.FraudReaction;
import hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.FraudReaction.FraudReactionRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static hbm.fraudDetectionSystem.ReactionEngine.Enum.ReactionEnum.SET_RESPCODE;

@Service
@Transactional
public class FraudBlackListServiceImpl implements FraudBlackListService {
    protected final Logger LOGGER = LoggerFactory.getLogger("U-BLACK");

    @PersistenceContext
    protected EntityManager em;
    protected final FraudBlackListRepository repository;
    protected final UserGroupService userGroupService;
    protected final UserService userService;
    protected final UploadExcelBlackListService uploadService;
    protected final ReactionEngine reactionEngine;
    protected final FraudReactionRepository fraudReactionRepository;


    @Autowired
    public FraudBlackListServiceImpl(FraudBlackListRepository repository, UserGroupService userGroupService, UserService userService, UploadExcelBlackListService uploadService, ReactionEngine reactionEngine, FraudReactionRepository fraudReactionRepository) {
        this.repository = repository;
        this.userGroupService = userGroupService;
        this.userService = userService;
        this.uploadService = uploadService;
        this.reactionEngine = reactionEngine;
        this.fraudReactionRepository = fraudReactionRepository;
    }


    @Override
    public List<FraudBlackList> listAllBlackList() {
        return repository.findByOrderByIdAsc();
    }

    @Override
    public String run(Map<String, String> preparedData) {
        List<FraudBlackList> fetchedBlackList = repository.findAllByOrderByIdAsc();
//        Map<String, Object> dictionaryData = new LinkedHashMap<>();
        List<ReactionContainer> dictionaryData = new LinkedList<>();
        Instant currentDate = Timestamp.valueOf(preparedData.get("sysdate")).toInstant();

        for (FraudBlackList entityType : fetchedBlackList) {
            Instant dateFrom = entityType.getDateIn().toInstant();
            Instant dateTo = entityType.getDateOut().toInstant();

            if (isDateIsNotExpired(currentDate, dateFrom, dateTo)) {
                String fetchData = preparedData.get(entityType.getEntityType());
                if (fetchData != null) {
                    if (fetchData.equals(entityType.getValue())) {
//                        this.reactionEngine
//                                .collect(entityType.getId(), BindingTypeEnum.BLACK_LIST.getName(), null, preparedData)
//                                .forEach(dictionaryData::putIfAbsent);

//                        this.reactionEngine.run(dictionaryData, Long.valueOf(preparedData.get("utrnno")));
//                        dictionaryData.putIfAbsent(SET_RESPCODE.getName(), "202");
//
//                        if (dictionaryData.get("isAlerted") != null)
//                            preparedData.put("isAlerted", "true");

                        this.reactionEngine
                                .collect(entityType.getId(), BindingTypeEnum.BLACK_LIST.getName(), "", preparedData)
                                .forEach(
                                        v -> {
                                            for (ReactionContainer reactionContainer : dictionaryData) {
                                                if (reactionContainer.getReactionEnum() == v.getReactionEnum()) {
                                                    return;
                                                }
                                            }
                                            dictionaryData.add(v);
                                        }
                                );

                        this.reactionEngine.run(dictionaryData);

                        Gson gson = new GsonBuilder()
                                .excludeFieldsWithoutExposeAnnotation()
                                .create();

                        preparedData.put("exReactions", gson.toJson(dictionaryData));

                        if (dictionaryData.stream().noneMatch(v -> v.getReactionEnum() == SET_RESPCODE)) {
                            dictionaryData.add(
                                    ReactionContainer.builder()
                                            .reactionEnum(SET_RESPCODE)
                                            .bindingId(String.valueOf(entityType.getId()))
                                            .bindingType(entityType.getEntityType())
                                            .zone(null)
                                            .reactionValue(
                                                    new SetResponseCode().run(
                                                            FraudReaction.builder()
                                                                    .actionValue("202")
                                                                    .build()
                                                    )
                                            ).build()
                            );
                        }

                        break;
                    }
                }
            }
        }

        return this.validateResponseCode(dictionaryData);
    }

    @Override
    public FraudBlackList addBlackList(String entityType, String value, Long userGroupId, Timestamp dateIn, Timestamp dateOut, Long initiatorId, String reason) {
        this.validateETypeAndValue(0L, entityType, value);
        UserGroup getUserGroup = userGroupService.findUserGroupById(userGroupId);
        User getUser = userService.findById(initiatorId);
        FraudBlackList addBlackList = new FraudBlackList();
        addBlackList.setEntityType(entityType);
        addBlackList.setValue(value);
        addBlackList.setDateIn(dateIn);
        addBlackList.setDateOut(dateOut);
        addBlackList.setReason(reason);
        addBlackList.setInitiator(getUser);
        addBlackList.setUserGroup(getUserGroup);
        repository.save(addBlackList);
        return addBlackList;
    }

    @Override
    public FraudBlackList addBlackListSystem(String entityType, String value, Timestamp dateIn, Timestamp dateOut, String reason) {
        FraudBlackList addBlackList = new FraudBlackList();
        addBlackList.setEntityType(entityType);
        addBlackList.setValue(value);
        addBlackList.setDateIn(dateIn);
        addBlackList.setDateOut(dateOut);
        addBlackList.setReason(reason);
        repository.save(addBlackList);
        return addBlackList;
    }

    @Override
    public FraudBlackList updateBlackList(Long currentId, String newEntityType, Timestamp dateIn, Timestamp dateOut, String newValue, Long newUserGroupId, Long newInitiatorId, String newReason) throws BlackListNotFoundException, BlackListExistException {
        this.validateETypeAndValue(currentId, newEntityType, newValue);
        UserGroup getUserGroup = userGroupService.findUserGroupById(newUserGroupId);
        User getUser = userService.findById(newInitiatorId);
        FraudBlackList currentBlackList = validateNewBlackList(currentId);
        currentBlackList.setEntityType(newEntityType);
        currentBlackList.setDateIn(dateIn);
        currentBlackList.setDateOut(dateOut);
        currentBlackList.setValue(newValue);
        currentBlackList.setReason(newReason);
        currentBlackList.setUserGroup(getUserGroup);
        currentBlackList.setInitiator(getUser);
        repository.save(currentBlackList);
        return currentBlackList;
    }

    private FraudBlackList validateNewBlackList(Long currentId) throws BlackListNotFoundException, BlackListExistException {
        FraudBlackList listByNewBlackListId = findBlacklistById(currentId);
        if (StringUtils.isNotBlank(String.valueOf(currentId))) {
            FraudBlackList currentBlackList = findBlacklistById(currentId);
            if (currentBlackList == null) {
                throw new BlackListNotFoundException(BlackListConstant.BlACKLIST_NOT_FOUND);
            }
            if (listByNewBlackListId != null && !currentBlackList.getId().equals(listByNewBlackListId.getId())) {
                throw new BlackListExistException(BlackListConstant.BLACK_LIST_EXIST);
            }
            return currentBlackList;
        } else {
            if (listByNewBlackListId != null) {
                throw new BlackListExistException(BlackListConstant.BLACK_LIST_EXIST);
            }
            return null;
        }
    }

    private FraudBlackList findBlacklistById(Long currentId) {
        return repository.findAllById(currentId);
    }

    @Override
    public void deleteBlackListValue(long id) {
        FraudBlackList fraudBlackList = repository.findAllById(id);
        repository.deleteById(fraudBlackList.getId());

        fraudReactionRepository.deleteAllByBindingTypeAndBindingId("BLACKLIST ", id);
    }


    @Override
    public void saveBlackListToDatabase(MultipartFile file, long initiatorId, long uGroupId) throws IOException {
        if (uploadService.isValidExcelFile(file)) {
            LOGGER.info(
                    String.format(
                            "Processing file with name: [%s]",
                            file.getOriginalFilename()
                    )
            );

            List<FraudBlackList> fraudBlackLists = uploadService.getBlacklistDataFromExcel(file.getInputStream(), initiatorId, uGroupId);
            LOGGER.info(
                    String.format(
                            "Successfully insert data with total: [%s]",
                            repository.saveAll(fraudBlackLists).size()
                    )
            );
        } else {
            throw new RuntimeException("The file isn't valid excel");
        }
    }

    @Override
    public List<FraudBlackList> searchFraudBlackList(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<FraudBlackList> query = cb.createQuery(FraudBlackList.class);
        Root<FraudBlackList> root = query.from(FraudBlackList.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!value.toString().isEmpty()) {
                            switch (key) {
                                case "userGroup":
                                    Long userGroupId = Long.parseLong(value.toString());
                                    Join<FraudBlackList, UserGroup> userGroupJoin = root.join("userGroup");
                                    predicates.add(cb.equal(userGroupJoin.get("id"), userGroupId));
                                    break;

                                case "initiator":
                                    int userId = Integer.parseInt(value.toString());
                                    Join<FraudBlackList, User> userJoin = root.join("initiator");
                                    predicates.add(cb.equal(userJoin.get("id"), userId));
                                    break;

                                case "dateIn":
                                    String dateFrom = (String) value;
                                    if (!dateFrom.isEmpty()) {
                                        predicates.add(cb.greaterThanOrEqualTo(root.get("dateIn"), Timestamp.valueOf(dateFrom)));
                                    }
                                    break;

                                case "dateOut":
                                    String dateTo = (String) value;
                                    if (!dateTo.isEmpty()) {
                                        predicates.add(cb.lessThanOrEqualTo(root.get("dateOut"), Timestamp.valueOf(dateTo)));
                                    }
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

        TypedQuery<FraudBlackList> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

//    protected String validateResponseCode(Map<String, Object> dictionaryData) {
//        Object respCode = dictionaryData.get(SET_RESPCODE.getName());
//        return respCode != null ?
//                respCode.toString() : null;
//    }

    protected String validateResponseCode(List<ReactionContainer> dictionaryData) {
        List<ReactionContainer> collection = dictionaryData.stream().filter(v -> v.getReactionEnum() == SET_RESPCODE).collect(Collectors.toList());
        ReactionContainer respCode = collection.size() > 0 ? collection.get(0) : null;
        return respCode != null ?
                respCode.getReactionValue() : null;
    }

    protected boolean isDateIsNotExpired(Instant currentDate, Instant dateFrom, Instant dateTo) {
        return currentDate.isAfter(dateFrom) && currentDate.isBefore(dateTo);
    }

    protected void validateETypeAndValue(long id, String eType, String value) {
        this.repository.findByEntityTypeAndValue(eType, value)
                .ifPresent(v1 -> {
                    if (v1.getId() != id)
                        throw new RuntimeException("Data already exist");
                });
    }
}
