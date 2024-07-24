package hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.SpringLogic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.User;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.UserService;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroup;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroupService;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.constant.WhiteListConstant;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.exception.WhiteListExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.exception.WhiteListNotFoundException;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static hbm.fraudDetectionSystem.ReactionEngine.Enum.ReactionEnum.SET_RESPCODE;

@Service
@Transactional
public class FraudWhiteListServiceImpl implements FraudWhiteListService {
    protected final Logger LOGGER = LoggerFactory.getLogger("U-WHITE");

    @PersistenceContext
    protected EntityManager em;
    protected final FraudWhiteListRepository repository;
    protected final UserGroupService userGroupService;
    protected final UserService userService;
    protected final UploadExcelWhiteListService uploadService;
    protected final ReactionEngine reactionEngine;
    protected final FraudReactionRepository fraudReactionRepository;

    @Autowired
    public FraudWhiteListServiceImpl(FraudWhiteListRepository repository, UserGroupService userGroupService, UserService userService, UploadExcelWhiteListService uploadService, ReactionEngine reactionEngine, FraudReactionRepository fraudReactionRepository) {
        this.repository = repository;
        this.userGroupService = userGroupService;
        this.userService = userService;
        this.uploadService = uploadService;
        this.reactionEngine = reactionEngine;
        this.fraudReactionRepository = fraudReactionRepository;
    }


    @Override
    public List<FraudWhiteList> listAllWhiteList() {
        return repository.findByOrderByIdAsc();
    }

    @Override
    public FraudWhiteList addWhiteList(String entityType, String value, Long userGroupId, Timestamp dateIn, Timestamp dateOut, Long initiatorId, String reason) {
        this.validateETypeAndValue(0L, entityType, value);
        UserGroup getUserGroup = userGroupService.findUserGroupById(userGroupId);
        User getUser = userService.findById(initiatorId);
        FraudWhiteList addWhiteList = new FraudWhiteList();
        addWhiteList.setEntityType(entityType);
        addWhiteList.setValue(value);
        addWhiteList.setDateIn(dateIn);
        addWhiteList.setDateOut(dateOut);
        addWhiteList.setReason(reason);
        addWhiteList.setInitiator(getUser);
        addWhiteList.setUserGroup(getUserGroup);
        repository.save(addWhiteList);
        return addWhiteList;
    }

    @Override
    public FraudWhiteList addWhiteListSystem(String entityType, String value, Timestamp dateIn, Timestamp dateOut, String reason) {
        FraudWhiteList addWhiteList = new FraudWhiteList();
        addWhiteList.setEntityType(entityType);
        addWhiteList.setValue(value);
        addWhiteList.setDateIn(dateIn);
        addWhiteList.setDateOut(dateOut);
        addWhiteList.setReason(reason);
        repository.save(addWhiteList);
        return addWhiteList;
    }


    @Override
    public FraudWhiteList updateWhiteList(Long currentId, String newEntityType, Timestamp dateIn, Timestamp dateOut, String newValue, Long newUserGroupId, Long newInitiatorId, String newReason) throws WhiteListExistException, WhiteListNotFoundException {
        this.validateETypeAndValue(currentId, newEntityType, newValue);
        UserGroup getUserGroup = userGroupService.findUserGroupById(newUserGroupId);
        User getUser = userService.findById(newInitiatorId);
        FraudWhiteList currentWhiteList = validateNewWhiteList(currentId);
        currentWhiteList.setEntityType(newEntityType);
        currentWhiteList.setDateIn(dateIn);
        currentWhiteList.setDateOut(dateOut);
        currentWhiteList.setValue(newValue);
        currentWhiteList.setReason(newReason);
        currentWhiteList.setUserGroup(getUserGroup);
        currentWhiteList.setInitiator(getUser);
        repository.save(currentWhiteList);
        return currentWhiteList;
    }

    private FraudWhiteList validateNewWhiteList(Long currentId) throws WhiteListNotFoundException, WhiteListExistException {
        FraudWhiteList listByNewWhiteListId = findWhiteListById(currentId);
        if (StringUtils.isNotBlank(String.valueOf(currentId))) {
            FraudWhiteList currentWhiteList = findWhiteListById(currentId);
            if (currentWhiteList == null) {
                throw new WhiteListNotFoundException(WhiteListConstant.WHITE_LIST_NOT_FOUND);
            }
            if (listByNewWhiteListId != null && !currentWhiteList.getId().equals(listByNewWhiteListId.getId())) {
                throw new WhiteListExistException(WhiteListConstant.WHITE_LIST_EXIST);
            }
            return currentWhiteList;
        } else {
            if (listByNewWhiteListId != null) {
                throw new WhiteListExistException(WhiteListConstant.WHITE_LIST_EXIST);
            }
            return null;
        }
    }

    private FraudWhiteList findWhiteListById(Long currentId) {
        return repository.findAllById(currentId);
    }

    @Override
    public void deleteWhiteListValue(long id) {
        FraudWhiteList fraudWhiteList = repository.findAllById(id);
        repository.deleteById(fraudWhiteList.getId());

        fraudReactionRepository.deleteAllByBindingTypeAndBindingId("WHITELIST", id);
    }

    @Override
    public String run(Map<String, String> preparedData) {
        List<FraudWhiteList> fetchedWhiteList = repository.findAllByOrderByIdAsc();
//        Map<String, Object> dictionaryData = new LinkedHashMap<>();
        List<ReactionContainer> dictionaryData = new LinkedList<>();
        Instant currentDate = Timestamp.valueOf(preparedData.get("sysdate")).toInstant();

        for (FraudWhiteList entityType : fetchedWhiteList) {
            Instant dateFrom = entityType.getDateIn().toInstant();
            Instant dateTo = entityType.getDateOut().toInstant();

            if (isDateIsNotExpired(currentDate, dateFrom, dateTo)) {
                String fetchData = preparedData.get(entityType.getEntityType());

                if (fetchData != null) {
                    if (fetchData.equals(entityType.getValue())) {
//                        this.reactionEngine
//                                .collect(entityType.getId(), BindingTypeEnum.WHITE_LIST.getName(), null, preparedData)
//                                .forEach(dictionaryData::putIfAbsent);

                         this.reactionEngine
                                .collect(entityType.getId(), BindingTypeEnum.WHITE_LIST.getName(), "", preparedData)
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

//                        this.reactionEngine.run(dictionaryData, Long.valueOf(preparedData.get("utrnno")));
//                        dictionaryData.putIfAbsent(SET_RESPCODE.getName(), "201");
//                        if (dictionaryData.get("isAlerted") != null)
//                            preparedData.put("isAlerted", "true");

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
                                                                    .actionValue("201")
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
    public void saveWhiteListToDatabase(MultipartFile file, long initiatorId, long uGroupId) throws IOException {
        if (uploadService.isValidExcelFile(file)) {
            LOGGER.info(
                    String.format(
                            "Processing file with name: [%s]",
                            file.getOriginalFilename()
                    )
            );

            List<FraudWhiteList> fraudWhiteLists = uploadService.getWhiteListDataFromExcel(
                    file.getInputStream(),
                    initiatorId,
                    uGroupId
            );
            LOGGER.info(
                    String.format(
                            "Successfully insert data with total: [%s]",
                            repository.saveAll(fraudWhiteLists).size()
                    )
            );
        } else {
            throw new RuntimeException("The file isn't valid excel");
        }
    }

    @Override
    public List<FraudWhiteList> searchWhiteList(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<FraudWhiteList> query = cb.createQuery(FraudWhiteList.class);
        Root<FraudWhiteList> root = query.from(FraudWhiteList.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!value.toString().isEmpty()) {
                            switch (key) {
                                case "userGroup":
                                    Long userGroupId = Long.parseLong(value.toString());
                                    Join<FraudWhiteList, UserGroup> userGroupJoin = root.join("userGroup");
                                    predicates.add(cb.equal(userGroupJoin.get("id"), userGroupId));
                                    break;

                                case "initiator":
                                    int userId = Integer.parseInt(value.toString());
                                    Join<FraudWhiteList, User> userJoin = root.join("initiator");
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

        TypedQuery<FraudWhiteList> typedQuery = this.em.createQuery(query);
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
