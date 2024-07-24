package hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.SpringLogic.Case;


import hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.SpringLogic.FraudBlackListService;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.SpringLogic.CaseHistory.CaseHistoryService;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.enumeration.ActionType;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.exception.*;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListValue.FraudValue;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListValue.FraudValueService;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.FraudListExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.FraudValueExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.FraudValueNotFound;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.User;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.UserService;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.SpringLogic.FraudWhiteListService;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans.CurrTrans;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans.CurrTransServiceImpl;
import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.UserNotification.UserNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.sql.Timestamp;
import java.util.*;

import static hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.constant.CaseConstant.*;
import static hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.enumeration.ActionType.*;


@Service
public class CaseServiceImpl implements CaseService {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @PersistenceContext
    private EntityManager em;
    private CaseRepository repository;
    private CaseHistoryService historyService;
    private UserService userService;
    private FraudValueService valueService;
    private FraudWhiteListService whiteListService;
    private FraudBlackListService blackListService;
    private UserNotificationService notificationService;
    private final CurrTransServiceImpl currTransService;


    @Autowired
    public CaseServiceImpl(CaseRepository repository, CaseHistoryService historyService, UserService userService, FraudValueService valueService, FraudWhiteListService fraudWhiteListService, FraudBlackListService fraudBlackListService, UserNotificationService notificationService, CurrTransServiceImpl currTransService) {
        this.repository = repository;
        this.historyService = historyService;
        this.userService = userService;
        this.valueService = valueService;
        this.whiteListService = fraudWhiteListService;
        this.blackListService = fraudBlackListService;
        this.notificationService = notificationService;
        this.currTransService = currTransService;
    }

    @Override
    public List<Map<Object, Object>> findAllCase() {
        return repository.findByOrderByLastUpdateDesc();
    }

    @Override
    public List<Map<Object, Object>> findCaseOrderByLockedByAndCaseId(String username) {
        User user = userService.findByUsername(username);
        return repository.findCaseOrderByLockedByAndCaseId(user.getId(), username);
    }

    @Override
    public Case lockCase(long caseId, String username) throws Exception {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        Case getCase = validateCaseId(caseId,OPEN_ALERT.name(), username);
        getCase.setIsLocked(true);
        getCase.setLockedBy(username);
        getCase.setLastUpdate(ts);
        repository.save(getCase);
        LOGGER.info("Alert with case id : " + caseId + " Locked By : " + username);
        historyService.addCaseHistory(OPEN_ALERT.name(),username,ts,LOCKCASE_WITH_USERNAME + username,caseId);
        return getCase;
    }


    @Override
    public Case unlockCase(long caseId, String username) throws Exception {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        Case getCase = validateCaseId(caseId, CLOSE_ALERT.name(), username);
        getCase.setIsLocked(false);
        getCase.setLockedBy(null);
        getCase.setLastUpdate(timestamp);
        repository.save(getCase);
        LOGGER.info("Alert with case id : " + caseId + " unlocked By : " + username);
        historyService.addCaseHistory(CLOSE_ALERT.name(), username,timestamp,UNLOCKCASE_WITH_USERNAME + username, caseId);
        return getCase;
    }

    @Override
    public Case clasifyAlert(long caseId, String initiator, int clasType, String clasifiedComment) throws Exception {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        String clasTypeMap = validateClasType(clasType);
        Case getCase = validateCaseId(caseId,CLASIFY_ALERT.name(), initiator);
        getCase.setClassifiedDate(timestamp);
        getCase.setClasificationType(clasType);
        getCase.setClasifiedComment(clasifiedComment);
        getCase.setInitiator(initiator);
        getCase.setLastUpdate(timestamp);
        getCase.setIsClassified(true);
        repository.save(getCase);

        switch (clasType) {
            case 10:
                this.currTransService.assignFraudFlag(getCase.getUtrnno(), 0);
                break;
            case 20:
                this.currTransService.assignFraudFlag(getCase.getUtrnno(), 1);
                break;
            case 30:
                this.currTransService.assignFraudFlag(getCase.getUtrnno(), 2);
                break;
        }

        LOGGER.info("Alert with case id : " + caseId + ", is " + clasTypeMap);
        historyService.addCaseHistory(CLASIFY_ALERT.name(),initiator,timestamp, CLASIFY_AS + clasTypeMap + COMMENTED + clasifiedComment,caseId);
        return getCase;
    }

    @Override
    public Case takeAction(long caseId, String initiator, int actionType, String caseComment, String listId, String value, String reason, String entityType, String datein, String dateout, String userGroupId) throws Exception {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        String actionTypeMap = validateActionType(caseId,actionType,listId, value, initiator, reason, entityType, datein, dateout, userGroupId);
        Case getCase = validateCaseId(caseId,actionTypeMap, initiator);
        getCase.setCaseId(caseId);
        getCase.setInitiator(initiator);
        getCase.setCaseComment(caseComment);
        getCase.setIsActioned(true);
        getCase.setLastUpdate(timestamp);
        getCase.setActionDate(timestamp);
        getCase.setActionType(actionType);
        getCase.setIsForwarded(false);
        getCase.setForwardedTo(null);
        repository.save(getCase);
        LOGGER.info("Alert with case id : " + caseId + ", action : " + actionTypeMap, " action by : " + initiator);
        historyService.addCaseHistory(actionTypeMap, initiator, timestamp, ACTION_IS + generateInfoAction(actionType, "", listId, value) + COMMENTED + caseComment, caseId);
        return getCase;
    }

    @Override
    public Case takeActionForwardedTo(Long caseId, String initiator, int actionType, String caseComment, long userForwardedTo, String listId, String value, String reason, String entityType, String datein, String dateout, String userGroupId) throws Exception {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        String  actionTypeMap = validateActionType(caseId,actionType, listId, value, initiator, reason, entityType, datein, dateout, userGroupId);
        User getUser = validateForwardedTo(userForwardedTo);
        Case getCase = validateCaseId(caseId,actionTypeMap, initiator);
        getCase.setCaseId(caseId);
        getCase.setInitiator(initiator);
        getCase.setCaseComment(caseComment);
        getCase.setIsActioned(true);
        getCase.setLastUpdate(timestamp);
        getCase.setActionDate(timestamp);
        getCase.setActionType(actionType);
        getCase.setIsForwarded(true);
        getCase.setForwardedTo(getUser.getId());
        repository.save(getCase);
        LOGGER.info("Alert with case id : " + caseId + ", action : " + actionTypeMap, " action by : " + initiator);
        notificationService.addNotification(initiator, "Alert Investigation", "USER",getUser.getUsername(), timestamp, initiator + " Forward the case " + "[" + caseId + "]" +  " for you");
        historyService.addCaseHistory(actionTypeMap, initiator, timestamp, ACTION_IS + generateInfoAction(actionType, getUser.getUsername(), listId, value), caseId);
        return getCase;
    }

    @Override
    public void generateCase(Map<String, String> transDetails) throws InitiatorException {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        Case aCase = new Case();
        Long utrnno = Long.valueOf(transDetails.get("utrnno"));
        aCase.setUtrnno(utrnno);
        aCase.setHpan(transDetails.get("hpan"));
        aCase.setInitiator("SYSTEM");
        aCase.setClasificationType(0);
        aCase.setActionType(0);
        aCase.setIsClassified(false);
        aCase.setIsActioned(false);
        aCase.setIsClassified(false);
        aCase.setIsForwarded(false);
        aCase.setIsLocked(false);
        aCase.setAlertDate(ts);
        aCase.setLastUpdate(ts);
        repository.save(aCase);

        String message = String.format(
                "ALERT GENERATED BY BINDING ID: [%s], BINDING TYPE: [%s] AND ZONE: [%s] WITH CASE ID: [%s]",
                transDetails.get("bindingId"), transDetails.get("bindingType"), transDetails.get("zone"), aCase.getCaseId()
        );
        historyService.addCaseHistory(INITIAL_ACTION,INITIAL_INITIATOR,ts, message, aCase.getCaseId());
    }

    @Override
    public List<Object> generateReportAlertStatisticByRule(Timestamp reportStartDate, Timestamp reportEndDate) {
        return repository.findReportAlertStatisticByRules(reportStartDate, reportEndDate);
    }

    @Override
    public List<Object> generateReportAlertStatisticTotal(Timestamp reportStartDate, Timestamp reportEndDate) {
        return repository.findReportAlertStatisticTotal(reportStartDate, reportEndDate);
    }

    @Override
    public List<Object>generateReportNotAlertedAlerted(Timestamp reportStartDate, Timestamp reportEndDate){
        return repository.findReportNotAlertedAlerted(reportStartDate, reportEndDate);
    }

    @Override
    public List<Map<String, Object>> searchAlert(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
        Root<CurrTrans> rootCt = query.from(CurrTrans.class);
        Root<Case> rootCs = query.from(Case.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (!key.equals("dateFrom") && !key.equals("dateTo") && value != null)
                        if (!value.toString().isEmpty())
                            predicates.add(cb.equal(rootCs.get(key), value));
                });

        String dateFrom = (String) reqBody.get("dateFrom");
        String dateTo = (String) reqBody.get("dateTo");

        if (dateFrom != null && dateTo != null) {
            if (!dateFrom.isEmpty() && !dateTo.isEmpty()) {
                predicates.add(cb.between(rootCs.get("alertDate"), Timestamp.valueOf(dateFrom), Timestamp.valueOf(dateTo)));
            }
        } else {
            if (dateFrom != null) {
                if (!dateFrom.isEmpty())
                    predicates.add(cb.greaterThanOrEqualTo(rootCs.get("alertDate"), Timestamp.valueOf(dateFrom)));
            }

            if (dateTo != null) {
                if (!dateTo.isEmpty())
                    predicates.add(cb.lessThanOrEqualTo(rootCs.get("alertDate"), Timestamp.valueOf(dateTo)));
            }
        }

        predicates.add(cb.equal(rootCs.get("utrnno"), rootCt.get("utrnno")));

        Order order = cb.asc(rootCs.get("caseId"));
        query.orderBy(order);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        query.multiselect(
                rootCs.alias("cs"),
                rootCt.alias("ct")
        );

        List<Tuple> tuples = this.em.createQuery(query).getResultList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Tuple tuple : tuples) {
            Map<String, Object> row = new LinkedHashMap<>();

            Case caseEntity = tuple.get("cs", Case.class);
            row.put("case_id", caseEntity.getCaseId());
            row.put("action_date", caseEntity.getActionDate());
            row.put("action_type", caseEntity.getActionType());
            row.put("case_comment", caseEntity.getCaseComment());
            row.put("clasification_type", caseEntity.getClasificationType());
            row.put("clasified_comment", caseEntity.getClasifiedComment());
            row.put("classified_date", caseEntity.getClassifiedDate());
            row.put("case_hpan", caseEntity.getHpan());
            row.put("initiator", caseEntity.getInitiator());
            row.put("is_actioned", caseEntity.getIsActioned());
            row.put("is_classified", caseEntity.getIsClassified());
            row.put("is_forwarded", caseEntity.getIsForwarded());
            row.put("is_locked", caseEntity.getIsLocked());
            row.put("last_update", caseEntity.getLastUpdate());
            row.put("locked_by", caseEntity.getLockedBy());
            row.put("case_utrnno", caseEntity.getUtrnno());
            row.put("forwarded_to", caseEntity.getForwardedTo());

            CurrTrans currTransEntity = tuple.get("ct", CurrTrans.class);
            row.put("utrnno", currTransEntity.getUtrnno());
            row.put("pid", currTransEntity.getPid());
            row.put("acct1", currTransEntity.getAcct1());
            row.put("acct2", currTransEntity.getAcct2());
            row.put("acq_instit_code", currTransEntity.getAcqInstitCode());
            row.put("iss_instit_code", currTransEntity.getIssInstitCode());
            row.put("des_instit_code", currTransEntity.getDesInstitCode());
            row.put("amount", currTransEntity.getAmount());
            row.put("fee_amount", currTransEntity.getFeeAmount());
            row.put("acct_balance", currTransEntity.getAcctBalance());
            row.put("currency", currTransEntity.getCurrency());
            row.put("hpan", currTransEntity.getHpan());
            row.put("trans_type", currTransEntity.getTransType());
            row.put("pos_data_code", currTransEntity.getPosDataCode());
            row.put("prc_code", currTransEntity.getPrcCode());
            row.put("ref_num", currTransEntity.getRefNum());
            row.put("ext_resp_code", currTransEntity.getExtRespCode());
            row.put("resp_code", currTransEntity.getRespCode());
            row.put("resp_code_desc", currTransEntity.getRespCodeDesc());
            row.put("stan", currTransEntity.getStan());
            row.put("merchant_type", currTransEntity.getMerchantType());
            row.put("sysdate", currTransEntity.getSysdate());
            row.put("terminal_id", currTransEntity.getTerminalId());
            row.put("terminal_address", currTransEntity.getTerminalAddress());
            row.put("fraud_flags", currTransEntity.getFraudFlags());
            row.put("is_alerted", currTransEntity.getIsAlerted());
            result.add(row);
        }

        return result;
    }

    private String generateInfoAction(int actionType, String username, String listId, String value) {
        String result = null;
        if (actionType == 91){
            result = ADD_ALERT_COMMENT.getName();
        }
        if (actionType == 92){
            result = FORWARDED_TO.getName() + " " + username;
        }
        if (actionType == 93){
            result = ADD_CARD_TO_LIST.getName() + " value :  [" + value + "], to listId : [" + listId + "]";
        }
        if (actionType == 94){
            result = ADD_ACCOUNT_TO_LIST.getName() + " value :  [" + value + "], to listId : [" + listId + "]";
        }
        if (actionType == 95){
            result = ADD_MERCHANT_TO_LIST.getName() + " value :  [" + value + "], to listId : [" + listId + "]";
        }
        if (actionType == 96){
            result = ADD_TERMINAL_TO_LIST.getName() + " value :  [" + value + "], to listId : [" + listId + "]";
        }
        if (actionType == 70){
            result = REMOVE_CARD_TO_LIST.getName() + " value :  [" + value + "], from listId : [" + listId + "]";
        }
        if (actionType == 71){
            result = REMOVE_ACCOUNT_TO_LIST.getName() + " value :  [" + value + "], from listId : [" + listId + "]";
        }
        if (actionType == 72){
            result = REMOVE_MERCHANT_TO_LIST.getName() + " value :  [" + value + "], from listId : [" + listId + "]";
        }
        if (actionType == 73){
            result = REMOVE_TERMINAL_TO_LIST.getName() + " value :  [" + value + "], from listId : [" + listId + "]";
        }
        if (actionType == 82){
            result = PUT_CARD_IN_WHITE_LIST.getName() + " value :  [" + value + "]";
        }
        if (actionType == 83){
            result = PUT_ACCOUNT_IN_WHITE_LIST.getName() + " value :  [" + value + "]";
        }
        if (actionType == 84){
            result = PUT_MERCHANT_IN_WHITE_LIST.getName() + " value :  [" + value + "]";
        }
        if (actionType == 85){
            result = PUT_TERMINAL_IN_WHITE_LIST.getName() + " value :  [" + value + "]";
        }
        if (actionType == 86){
            result = PUT_CARD_IN_BLACK_LIST.getName() + " value :  [" + value + "]";
        }
        if (actionType == 87){
            result = PUT_ACCOUNT_IN_BLACK_LIST.getName() + " value :  [" + value + "]";
        }
        if (actionType == 88){
            result = PUT_MERCHANT_IN_BLACK_LIST.getName() + " value :  [" + value + "]";
        }
        if (actionType == 89){
            result = PUT_TERMINAL_IN_BLACK_LIST.getName() + " value :  [" + value + "]";
        }
        return result;
    }

    private String validateActionType(long caseId, int actionType, String listId, String value, String initiator, String reason, String entityType, String datein, String dateout, String userGroupId) throws FraudListExistException, FraudValueNotFound, FraudValueExistException {
        String result = null;
        if (actionType == 91){
            result = ADD_ALERT_COMMENT.name();
        }
        if (actionType == 92){
            result = FORWARDED_TO.name();
        }
        if (actionType == 93){
            valueService.addValue(value,initiator, Long.valueOf(listId));
            result = ADD_CARD_TO_LIST.name();
        }
        if (actionType == 94){
            valueService.addValue(value,initiator, Long.valueOf(listId));
            result = ADD_ACCOUNT_TO_LIST.name();
        }
        if (actionType == 95){
            valueService.addValue(value,initiator, Long.valueOf(listId));
            result = ADD_MERCHANT_TO_LIST.name();
        }
        if (actionType == 96){
            valueService.addValue(value,initiator, Long.valueOf(listId));
            result = ADD_TERMINAL_TO_LIST.name();
        }
        if (actionType == 70){
            FraudValue getValue = valueService.findByValue(value);
            valueService.deleteFraudListValue(getValue.getId());
            result = REMOVE_CARD_TO_LIST.name();
        }
        if (actionType == 71){
            FraudValue getValue = valueService.findByValue(value);
            valueService.deleteFraudListValue(getValue.getId());
            result = REMOVE_ACCOUNT_TO_LIST.name();
        }
        if (actionType == 72){
            FraudValue getValue = valueService.findByValue(value);
            valueService.deleteFraudListValue(getValue.getId());
            result = REMOVE_MERCHANT_TO_LIST.name();
        }
        if (actionType == 73){
            FraudValue getValue = valueService.findByValue(value);
            valueService.deleteFraudListValue(getValue.getId());
            result = REMOVE_TERMINAL_TO_LIST.name();
        }
        if (actionType == 82){
            //timestamp format : 2023-04-15 07:00:00
            long groupId = Long.parseLong(userGroupId);
            User user = userService.findByUsername(initiator);
            whiteListService.addWhiteList(entityType,value,groupId,Timestamp.valueOf(datein), Timestamp.valueOf(dateout),user.getId(),reason);
            result = PUT_CARD_IN_WHITE_LIST.name();
        }
        if (actionType == 83){
            //timestamp format : 2023-04-15 07:00:00
            long groupId = Long.parseLong(userGroupId);
            User user = userService.findByUsername(initiator);
            whiteListService.addWhiteList(entityType,value,groupId,Timestamp.valueOf(datein), Timestamp.valueOf(dateout),user.getId(),reason);
            result = PUT_ACCOUNT_IN_WHITE_LIST.name();
        }
        if (actionType == 84){
            //timestamp format : 2023-04-15 07:00:00
            long groupId = Long.parseLong(userGroupId);
            User user = userService.findByUsername(initiator);
            whiteListService.addWhiteList(entityType,value,groupId,Timestamp.valueOf(datein), Timestamp.valueOf(dateout),user.getId(),reason);
            result = PUT_MERCHANT_IN_WHITE_LIST.name();
        }
        if (actionType == 85){
            //timestamp format : 2023-04-15 07:00:00
            long groupId = Long.parseLong(userGroupId);
            User user = userService.findByUsername(initiator);
            whiteListService.addWhiteList(entityType,value,groupId,Timestamp.valueOf(datein), Timestamp.valueOf(dateout),user.getId(),reason);
            result = PUT_TERMINAL_IN_WHITE_LIST.name();
        }
        if (actionType == 86){
            //timestamp format : 2023-04-15 07:00:00
            long groupId = Long.parseLong(userGroupId);
            User user = userService.findByUsername(initiator);
            blackListService.addBlackList(entityType,value,groupId,Timestamp.valueOf(datein), Timestamp.valueOf(dateout),user.getId(),reason);
            result = PUT_CARD_IN_BLACK_LIST.name();
        }
        if (actionType == 87){
            //timestamp format : 2023-04-15 07:00:00
            long groupId = Long.parseLong(userGroupId);
            User user = userService.findByUsername(initiator);
            blackListService.addBlackList(entityType,value,groupId,Timestamp.valueOf(datein), Timestamp.valueOf(dateout),user.getId(),reason);
            result = PUT_ACCOUNT_IN_BLACK_LIST.name();
        }
        if (actionType == 88){
            //timestamp format : 2023-04-15 07:00:00
            long groupId = Long.parseLong(userGroupId);
            User user = userService.findByUsername(initiator);
            blackListService.addBlackList(entityType,value,groupId,Timestamp.valueOf(datein), Timestamp.valueOf(dateout),user.getId(),reason);
            result = PUT_MERCHANT_IN_BLACK_LIST.name();
        }
        if (actionType == 89){
            //timestamp format : 2023-04-15 07:00:00
            long groupId = Long.parseLong(userGroupId);
            User user = userService.findByUsername(initiator);
            blackListService.addBlackList(entityType,value,groupId,Timestamp.valueOf(datein), Timestamp.valueOf(dateout),user.getId(),reason);
            result = PUT_TERMINAL_IN_BLACK_LIST.name();
        }
        return result;
    }

    private String validateClasType(int clasType) {
        String result = "";
        if (clasType == 10){
            result = "NEGATIVE";
        }else if (clasType == 20){
            result = "SUSPICIOUS";
        }else if (clasType == 30){
            result = "POSITIVE";
        }else if (clasType == 40){
            result = "POSTPONE";//TODO::DITUNDA SAMBIL CARI ROADMAP NYA
        }else {
            result = "Can't Mapping value of Clasification Type";
        }
        return result;
    }

    private Case validateCaseId(long caseId, String actionType, String username) throws Exception {
        Case getCase = findCaseId(caseId);
        Case findUsername = findAllByUsername(username);
        ActionType getActionType = ActionType.findByNameActionType(actionType);
        if (getCase == null){
            LOGGER.error("Case Not Found by case id: " + caseId);
            throw new CaseIdNotFoundException("Case Not Found by case id: " + caseId);
        }
        if (findUsername != null && findUsername.getCaseId() != caseId){
            LOGGER.error("username : " + findUsername + " still lock case id : " + findUsername.getCaseId());
            LOGGER.error("Anomaly activity from user : " + username);
            throw new InitiatorAnomalyException("Please Unlock Another Case " + username + "!");
        }
        else {
            if (actionType.equals(OPEN_ALERT.name()) && getCase.getIsLocked()){
                LOGGER.error("Case Already Locked");
                throw new CaseAlreadyLockedException("Case Already Locked");
            }if (actionType.equals(CLOSE_ALERT.name()) && !getCase.getIsLocked()){
                LOGGER.error("Case Already Unlocked");
                throw new CaseAlreadyUnlockedException("Case Already Unlocked");
            }if(actionType.equals(CLASIFY_ALERT.name()) && !getCase.getIsLocked()){
                LOGGER.error("Please Lock the Alert, First!");
                throw new CaseNeedLockException("Please Lock the Alert, First!");
            }
            if (actionType.equals(getActionType.name()) && !getCase.getIsLocked()){
                if (!actionType.equals(OPEN_ALERT.name())){
                    LOGGER.error("Please Lock the Alert, First!");
                    throw new CaseNeedLockException("Please Lock the Alert, First!");
                }
            }
        }
        return getCase;
    }

    @Override
    public Case findAllByUsername(String username) {
        return repository.findByLockedBy(username);
    }

    @Override
    public Map<String, Object> findCaseByUsername(String username) {
        return repository.findCaseByLockedBy(username);
    }

    @Override
    public Case findByUtrnno(long utrnno) {
        return repository.findByUtrnno(utrnno);
    }

    @Override
    public Case findByCaseId(long caseId) {
        return repository.findAllByCaseId(caseId);
    }

    private User validateForwardedTo(long forwardedToUserId) throws CaseIdNotFoundException {
        User getUser = userService.findById(forwardedToUserId);
        if (getUser == null){
            LOGGER.error("User Not Found by user id: " + forwardedToUserId);
            throw new CaseIdNotFoundException("User Not Found by user id: " + forwardedToUserId);
        }
        return getUser;
    }


    private Case findCaseId(long caseId) {
        return repository.findAllByCaseId(caseId);
    }

}
