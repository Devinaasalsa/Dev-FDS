package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans;

import com.google.common.base.CaseFormat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrAddtTrans.CurrAddtTrans;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrAddtTrans.CurrAddtTransRepository;
import hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper;
import hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.ExReaction.ReactionContainer;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleTriggered.RuleTriggered;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static hbm.fraudDetectionSystem.GeneralComponent.Constant.CurrTransConstant.*;

@Service
@Transactional
@Slf4j
public class CurrTransServiceImpl implements CurrTransService {
    @PersistenceContext
    private EntityManager entityManager;
    protected final CurrTransRepository currTransRepository;
    protected final CurrAddtTransRepository currAddtTransRepository;
    protected final CurrTransCacheService cacheService;
    protected final JdbcTemplate jdbcTemplate;

    @Autowired
    public CurrTransServiceImpl(@Qualifier("currTransRepository") CurrTransRepository currTransRepository, CurrAddtTransRepository currAddtTransRepository, CurrTransCacheService cacheService, JdbcTemplate jdbcTemplate) {
        this.currTransRepository = currTransRepository;
        this.currAddtTransRepository = currAddtTransRepository;
        this.cacheService = cacheService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<CurrTrans> findAllTransaction() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        return jdbcTemplate.query(GET_TRANSACTION_30DAYS_QUERY, (rs, rowNum) ->
                        new CurrTrans().builder()
                                .utrnno(rs.getLong("utrnno"))
                                .acct1(rs.getString("acct1"))
                                .acct2(rs.getString("acct2"))
                                .acctBalance(rs.getString("acct_balance"))
                                .acqInstitCode(rs.getString("acq_instit_code"))
                                .cvtAmount(rs.getString("cvt_amount"))
                                .amount(rs.getString("amount"))
                                .captDate(rs.getString("capt_date"))
                                .currency(rs.getString("currency"))
                                .desInstitCode(rs.getString("des_instit_code"))
                                .feeAmount(rs.getString("fee_amount"))
                                .fraudFlags(rs.getInt("fraud_flags"))
                                .hpan(rs.getString("hpan"))
                                .isAlerted(rs.getBoolean("is_alerted"))
                                .isReversal(rs.getBoolean("is_reversal"))
                                .issInstitCode(rs.getString("iss_instit_code"))
                                .merchantType(rs.getString("merchant_type"))
                                .pid(rs.getString("pid"))
                                .posDataCode(rs.getString("pos_data_code"))
                                .prcCode(rs.getString("prc_code"))
                                .refNum(rs.getString("ref_num"))
                                .extRespCode(rs.getString("ext_resp_code"))
                                .respCode(rs.getString("resp_code"))
                                .respCodeDesc(rs.getString("resp_code_desc"))
                                .stan(rs.getString("stan"))
                                .sysdate(rs.getString("sysdate"))
                                .terminalAddress(rs.getString("terminal_address"))
                                .terminalId(rs.getString("terminal_id"))
                                .transType(rs.getString("trans_type"))
                                .transTypeDesc(rs.getString("trans_type_desc"))
                                .ttime(rs.getString("ttime"))
                                .udate(rs.getString("udate"))
                                .ruleTrigger(rs.getLong("rule_trigger"))
                                .build()
                , currentDate, currentDate);
//                return jdbcTemplate.query(GET_TRANSACTION_30DAYS_QUERY, (rs, rowNum) ->
//                        new CurrTrans().builder()
//                                .utrnno(rs.getLong("utrnno"))
//                                .acct1(rs.getString("acct1"))
//                                .acct2(rs.getString("acct2"))
//                                .acctBalance(rs.getString("acct_balance"))
//                                .acqInstitCode(rs.getString("acq_instit_code"))
//                                .amount(rs.getString("amount"))
//                                .captDate(rs.getString("capt_date"))
//                                .currency(rs.getString("currency"))
//                                .desInstitCode(rs.getString("des_instit_code"))
//                                .feeAmount(rs.getString("fee_amount"))
//                                .fraudFlags(rs.getInt("fraud_flags"))
//                                .hpan(rs.getString("hpan"))
//                                .isAlerted(rs.getBoolean("is_alerted"))
//                                .issInstitCode(rs.getString("iss_instit_code"))
//                                .merchantType(rs.getString("merchant_type"))
//                                .pid(rs.getString("pid"))
//                                .posDataCode(rs.getString("pos_data_code"))
//                                .prcCode(rs.getString("prc_code"))
//                                .refNum(rs.getString("ref_num"))
//                                .respCode(rs.getString("resp_code"))
//                                .respCodeDesc(rs.getString("resp_code_desc"))
//                                .stan(rs.getString("stan"))
//                                .sysdate(rs.getString("sysdate"))
//                                .terminalAddress(rs.getString("terminal_address"))
//                                .terminalId(rs.getString("terminal_id"))
//                                .transType(rs.getString("trans_type"))
//                                .ttime(rs.getString("ttime"))
//                                .udate(rs.getString("udate"))
//                                .ruleTrigger(rs.getInt("rule_trigger"))
//                                .build()
//                );
    }

    @Override
    public List<CurrTrans> findAllByHpan(String hpan) {
//        return currTransRepository.findAllByHpanOrderBySysdate(hpan);
        return jdbcTemplate.query(GET_TRANSACTION_30DAYS_BY_HPAN_QUERY, (rs, rowNum) ->
                        new CurrTrans().builder()
                                .utrnno(rs.getLong("utrnno"))
                                .acct1(rs.getString("acct1"))
                                .acct2(rs.getString("acct2"))
                                .acctBalance(rs.getString("acct_balance"))
                                .acqInstitCode(rs.getString("acq_instit_code"))
                                .amount(rs.getString("amount"))
                                .captDate(rs.getString("capt_date"))
                                .currency(rs.getString("currency"))
                                .desInstitCode(rs.getString("des_instit_code"))
                                .feeAmount(rs.getString("fee_amount"))
                                .fraudFlags(rs.getInt("fraud_flags"))
                                .hpan(rs.getString("hpan"))
                                .isAlerted(rs.getBoolean("is_alerted"))
                                .isReversal(rs.getBoolean("is_reversal"))
                                .issInstitCode(rs.getString("iss_instit_code"))
                                .merchantType(rs.getString("merchant_type"))
                                .pid(rs.getString("pid"))
                                .posDataCode(rs.getString("pos_data_code"))
                                .prcCode(rs.getString("prc_code"))
                                .refNum(rs.getString("ref_num"))
                                .respCode(rs.getString("resp_code"))
                                .respCodeDesc(rs.getString("resp_code_desc"))
                                .stan(rs.getString("stan"))
                                .sysdate(rs.getString("sysdate"))
                                .terminalAddress(rs.getString("terminal_address"))
                                .terminalId(rs.getString("terminal_id"))
                                .transType(rs.getString("trans_type"))
                                .transTypeDesc(rs.getString("trans_type_desc"))
                                .ttime(rs.getString("ttime"))
                                .udate(rs.getString("udate"))
                                .cifId(rs.getString("cif_id"))
                                .ruleTrigger(rs.getLong("rule_trigger"))
                                .build(),
                hpan
        );
    }

    @Override
    public List<CurrTrans> findAllByHpanCustIdAcct1(String hpan, String custId, String acct1, String alertDate) {
        return jdbcTemplate.query(GET_TRANSACTION_30DAYS_BY_HPAN_CUST_ACCT1_QUERY(alertDate), (rs, rowNum) ->
                        new CurrTrans().builder()
                                .utrnno(rs.getLong("utrnno"))
                                .acct1(rs.getString("acct1"))
                                .acct2(rs.getString("acct2"))
                                .acctBalance(rs.getString("acct_balance"))
                                .acqInstitCode(rs.getString("acq_instit_code"))
                                .amount(rs.getString("amount"))
                                .captDate(rs.getString("capt_date"))
                                .currency(rs.getString("currency"))
                                .desInstitCode(rs.getString("des_instit_code"))
                                .feeAmount(rs.getString("fee_amount"))
                                .fraudFlags(rs.getInt("fraud_flags"))
                                .hpan(rs.getString("hpan"))
                                .isAlerted(rs.getBoolean("is_alerted"))
                                .isReversal(rs.getBoolean("is_reversal"))
                                .issInstitCode(rs.getString("iss_instit_code"))
                                .merchantType(rs.getString("merchant_type"))
                                .pid(rs.getString("pid"))
                                .posDataCode(rs.getString("pos_data_code"))
                                .prcCode(rs.getString("prc_code"))
                                .refNum(rs.getString("ref_num"))
                                .respCode(rs.getString("resp_code"))
                                .respCodeDesc(rs.getString("resp_code_desc"))
                                .stan(rs.getString("stan"))
                                .sysdate(rs.getString("sysdate"))
                                .terminalAddress(rs.getString("terminal_address"))
                                .terminalId(rs.getString("terminal_id"))
                                .transType(rs.getString("trans_type"))
                                .transTypeDesc(rs.getString("trans_type_desc"))
                                .ttime(rs.getString("ttime"))
                                .udate(rs.getString("udate"))
                                .cifId(rs.getString("cif_id"))
                                .ruleTrigger(rs.getLong("rule_trigger"))
                                .build(),
                hpan, custId, acct1
        );
    }

    @Override
    public CurrTrans assignFraudFlag(long utrnno, int fraudFlag) {
        CurrTrans findTrans = currTransRepository.findByUtrnno(utrnno);
        findTrans.setFraudFlags(fraudFlag);
        return currTransRepository.save(findTrans);
    }

    @Override
    public CurrTrans saveTransaction(Map<String, String> preparedData, List<CurrAddtTrans> preparedAddtData, boolean cvtAmount) throws ParseException {
        CurrTrans currTrans = setCurrTransData(preparedData, preparedAddtData, cvtAmount);

        currTransRepository.save(currTrans);
        currAddtTransRepository.saveAll(currTrans.getAddtData());
//        cacheService.addData(currTrans);
        return currTrans;
    }

    @Override
    public void updateAsAlertedByUtrnno(Long utrnno) {
        CurrTrans currTrans = findByUtrnno(utrnno);
        currTrans.setIsAlerted(true);
        currTransRepository.save(currTrans);
    }

    @Override
    public List<CurrTrans> searchTransaction(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<CurrTrans> query = cb.createQuery(CurrTrans.class);
        Root<CurrTrans> root = query.from(CurrTrans.class);

        Subquery<Long> subquery = query.subquery(Long.class);
        Root<RuleTriggered> subRoot = subquery.from(RuleTriggered.class);
        subquery.select(cb.count(subRoot.get("utrnno")));
        subquery.where(cb.equal(subRoot.get("utrnno"), root.get("utrnno")));

        // Main query
        Expression<Long> subqueryResult = subquery.getSelection();
        query.multiselect(
                root.get("utrnno"),
                root.get("pid"),
                root.get("acct1"),
                root.get("acct2"),
                root.get("acqInstitCode"),
                root.get("issInstitCode"),
                root.get("desInstitCode"),
                root.get("amount"),
                root.get("cvtAmount"),
                root.get("feeAmount"),
                root.get("acctBalance"),
                root.get("captDate"),
                root.get("currency"),
                root.get("hpan"),
                root.get("transType"),
                root.get("transTypeDesc"),
                root.get("posDataCode"),
                root.get("prcCode"),
                root.get("refNum"),
                root.get("extRespCode"),
                root.get("respCode"),
                root.get("respCodeDesc"),
                root.get("stan"),
                root.get("merchantType"),
                root.get("ttime"),
                root.get("udate"),
                root.get("sysdate"),
                root.get("terminalId"),
                root.get("terminalAddress"),
                root.get("fraudFlags"),
                root.get("isAlerted"),
                root.get("isReversal"),
                root.get("cifId"),
                subqueryResult.alias("ruleTrigger")
        );
        // Add predicates based on reqBody
        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!key.equals("dateFrom") && !key.equals("dateTo"))
                            if (!value.toString().isEmpty())
                                predicates.add(cb.equal(root.get(key), value));
                });

        String dateFrom = (String) reqBody.get("dateFrom");
        String dateTo = (String) reqBody.get("dateTo");

        if (!dateFrom.isEmpty() && !dateTo.isEmpty())
            predicates.add(cb.between(root.get("sysdate"), dateFrom, dateTo));
        else {
            if (!dateFrom.isEmpty())
                predicates.add(cb.greaterThanOrEqualTo(root.get("sysdate"), dateFrom));

            if (!dateTo.isEmpty())
                predicates.add(cb.lessThanOrEqualTo(root.get("sysdate"), dateTo));
        }

        query.groupBy(root.get("sysdate"), root.get("utrnno"));

        Order order = cb.desc(root.get("sysdate"));
        query.orderBy(order);

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<CurrTrans> typedQuery = this.entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    protected CurrTrans setCurrTransData(Map<String, String> preparedData, List<CurrAddtTrans> preparedAddtData, boolean cvtAmount) throws ParseException {
        CurrTrans currTrans = new CurrTrans();
        long utrnno = Long.parseLong(preparedData.get("utrnno"));
        currTrans.setUtrnno(utrnno);
        currTrans.setPid(trimValue(preparedData.get("pid")));
        currTrans.setAcct1(trimValue(preparedData.get("acct1")));
        currTrans.setAcct2(trimValue(preparedData.get("acct2")));
        currTrans.setAcqInstitCode(trimValue(preparedData.get("acqInstitCode")));
        currTrans.setIssInstitCode(trimValue(preparedData.get("issInstitCode")));
        currTrans.setDesInstitCode(trimValue(preparedData.get("desInstitCode")));
        currTrans.setFeeAmount(trimValue(preparedData.get("feeAmount")));
        currTrans.setAcctBalance(trimValue(preparedData.get("acctBalance")));
        currTrans.setCaptDate(trimValue(preparedData.get("captDate")));
        currTrans.setCurrency(trimValue(preparedData.get("currency")));
        currTrans.setHpan(trimValue(preparedData.get("hpan")));
        currTrans.setPrcCode(trimValue(preparedData.get("prcCode")));
        currTrans.setPosDataCode(trimValue(preparedData.get("postDataCode")));
        currTrans.setMerchantType(trimValue(preparedData.get("merchantType")));
        currTrans.setTerminalId(trimValue(preparedData.get("terminalId")));
        currTrans.setTerminalAddress(trimValue(preparedData.get("terminalAddress")));
        currTrans.setTtime(trimValue(preparedData.get("tTime")));
        currTrans.setUdate(trimValue(preparedData.get("uDate")));
        currTrans.setRefNum(trimValue(preparedData.get("rrn")));
        currTrans.setStan(trimValue(preparedData.get("stan")));
        currTrans.setSysdate(trimValue(preparedData.get("sysdate")));
        currTrans.setTransType(trimValue(preparedData.get("transType")));
        currTrans.setTransTypeDesc(trimValue(preparedData.get("transTypeDesc")));
        currTrans.setRespCode(trimValue(preparedData.get("respCode")));
        currTrans.setRespCodeDesc(trimValue(preparedData.get("respCodeDesc")));
        currTrans.setCifId(trimValue(preparedData.get("cifId")));
        currTrans.setExtRespCode(trimValue(preparedData.get("extRespCode")));
        currTrans.setAmount(trimValue(preparedData.get("amount")));

        if (preparedData.get("isReversal")!= null){
            currTrans.setIsReversal(Boolean.parseBoolean(preparedData.get("isReversal")) );
        }

        if (preparedData.get("amount") != null) {
            if (cvtAmount)
                this.setAmount(preparedData.get("amount"), currTrans);
            else
                this.setAmount(preparedData.get("amount") + "00", currTrans);
        }

        boolean isAlerted = this.validateIsAlerted(preparedData.get("isAlerted"));
        currTrans.setIsAlerted(isAlerted);

        this.setCurrAddtTransData(preparedAddtData, currTrans);

        if (this.isRuleTriggeredForThisTransaction(preparedData)) {
            TypeToken<List<RuleTriggered>> typeToken = new TypeToken<>() {
            };
            this.setTriggeredRules(
                    new Gson()
                            .fromJson(
                                    preparedData.get("triggeredRules"),
                                    typeToken.getType()
                            ),
                    currTrans);
        }

        if (this.isReactionExecutedForThisTransaction(preparedData)) {
            TypeToken<List<ReactionContainer>> typeToken = new TypeToken<>() {
            };
            this.setExecutedReactions(
                    new Gson()
                            .fromJson(
                                    preparedData.get("exReactions"),
                                    typeToken.getType()
                            ),
                    currTrans);
        }

        return currTrans;
    }


    private boolean validateIsAlerted(String isAlerted) {
        if (isAlerted != null) {
            return Boolean.parseBoolean(isAlerted);
        }
        return false;
    }

    protected void setAmount(String amount, CurrTrans currTrans) {
        if (amount != null) {
            if (!amount.isEmpty())
                currTrans.setCvtAmount(this.formatAmount(amount));
        }
    }

    protected void setCurrAddtTransData(List<CurrAddtTrans> preparedAddtData, CurrTrans currTrans) {
        List<CurrAddtTrans> fixedData = new LinkedList<>();
        for (CurrAddtTrans addtData : preparedAddtData) {
            addtData.setUtrnno(currTrans);
            fixedData.add(addtData);
        }
        currTrans.setAddtData(fixedData);
    }

    protected boolean isRuleTriggeredForThisTransaction(Map<String, String> preparedData) {
        return preparedData.get("triggeredRules") != null;
    }

    protected boolean isReactionExecutedForThisTransaction(Map<String, String> preparedData) {
        return preparedData.get("exReactions") != null;
    }

    protected void setTriggeredRules(List<RuleTriggered> triggeredRules, CurrTrans currTrans) {
        for (RuleTriggered triggeredRule : triggeredRules) {
            triggeredRule.setUtrnno(currTrans.getUtrnno());
        }
        currTrans.setRuleInfo(new HashSet<>(triggeredRules));
    }

    protected void setExecutedReactions(List<ReactionContainer> exReactions, CurrTrans currTrans) {
//        for (ReactionContainer exReaction : exReactions) {
//            exReaction.setUtrnno(currTrans.getUtrnno());
//        }
        currTrans.setExReactions(new HashSet<>(exReactions));
    }

    @Override
    public String findTransFieldByHistory(String historyBy, String historyValue, int depth, String transField) {
        List<String> fetchedValue;
        if (ServiceHelper.doesVariableExist(transField, CurrTrans.class)) {
            String cvtTransField = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, transField);
            fetchedValue = jdbcTemplate.query(
                    FIND_HISTORY_BY_TRANSFIELD_1_QUERY(cvtTransField, historyBy),
                    (rs, rowNum) -> rs.getString(cvtTransField.toUpperCase()),
                    historyValue, depth
            );
        } else {
            fetchedValue = jdbcTemplate.query(
                    FIND_HISTORY_BY_TRANSFIELD_2_QUERY(historyBy),
                    (rs, rowNum) -> rs.getString("value"),
                    historyValue, depth, transField
            );
        }

        return fetchedValue.size() > 0 ? fetchedValue.get(0) : "";
    }

    @Override
    public long limitCountMatched(String aggregatingEntity, String attribute, String startDateTime, String endDateTime, Map<String, String> inputData, String filtrationJoinQuery, String filtrationQuery) {
        if (ServiceHelper.doesVariableExist(aggregatingEntity, CurrTrans.class) &&
                ServiceHelper.doesVariableExist(attribute, CurrTrans.class)) {
            return jdbcTemplate.queryForObject(
                    COUNT_MATCH_AGGREGATE_CT_AND_ATTR_CT(filtrationJoinQuery, filtrationQuery, aggregatingEntity, attribute),
                    Integer.class,
                    startDateTime, endDateTime, inputData.get(aggregatingEntity), inputData.get(attribute)
            ).longValue();
        }

        if (!ServiceHelper.doesVariableExist(aggregatingEntity, CurrTrans.class) &&
                ServiceHelper.doesVariableExist(attribute, CurrTrans.class)) {
            return jdbcTemplate.queryForObject(
                    COUNT_MATCH_AGGREGATE_CAT_AND_ATTR_CT(filtrationJoinQuery, filtrationQuery, aggregatingEntity, attribute),
                    Integer.class,
                    startDateTime, endDateTime, inputData.get(aggregatingEntity), inputData.get(attribute)
            ).longValue();
        }

        if (ServiceHelper.doesVariableExist(aggregatingEntity, CurrTrans.class) &&
                !ServiceHelper.doesVariableExist(attribute, CurrTrans.class)) {
            return jdbcTemplate.queryForObject(
                    COUNT_MATCH_AGGREGATE_CT_AND_ATTR_CAT(filtrationJoinQuery, filtrationQuery, aggregatingEntity, attribute),
                    Integer.class,
                    startDateTime, endDateTime, inputData.get(aggregatingEntity), inputData.get(attribute)
            ).longValue();
        }

        if (!ServiceHelper.doesVariableExist(aggregatingEntity, CurrTrans.class) &&
                !ServiceHelper.doesVariableExist(attribute, CurrTrans.class)) {
            return jdbcTemplate.queryForObject(
                    COUNT_MATCH_AGGREGATE_CAT_AND_ATTR_CAT(filtrationJoinQuery, filtrationQuery, aggregatingEntity, attribute),
                    Integer.class,
                    startDateTime, endDateTime, inputData.get(aggregatingEntity), inputData.get(attribute)
            ).longValue();
        }

        //running query (select count(*) from curr_trans where attribute = attribute(value) and aggregatingEntity = aggregatingEntity(value) and sysdate between startDateTime and endDateTime;
//        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Long> queryCount = cb.createQuery(Long.class);
//        Root<CurrTrans> root = queryCount.from(CurrTrans.class);
//
//        Predicate attributeCond = cb.equal(root.get(attribute), inputData.get(attribute));
//        Predicate aggEntity = cb.equal(root.get(aggregatingEntity), inputData.get(aggregatingEntity));
//        Predicate dateCondition = cb.between(root.get("sysdate"), startDateTime, endDateTime);
//
//        Predicate finalCondition = cb.and(attributeCond, aggEntity, dateCondition);
//        queryCount.select(cb.count(root)).where(finalCondition);
//
//        return entityManager.createQuery(queryCount).getSingleResult().intValue();
        return 0;
    }

    @Override
    public long limitCountDifferent(String aggregatingEntity, String attribute, String startDate, String endDate, Map<String, String> inputData, String filtrationJoinQuery, String filtrationQuery) {
        if (ServiceHelper.doesVariableExist(aggregatingEntity, CurrTrans.class) &&
                ServiceHelper.doesVariableExist(attribute, CurrTrans.class)) {
            return jdbcTemplate.queryForObject(
                    COUNT_DIFF_AGGREGATE_CT_AND_ATTR_CT(filtrationJoinQuery, filtrationQuery, aggregatingEntity, attribute),
                    Integer.class,
                    startDate, endDate,
                    inputData.get(aggregatingEntity), inputData.get(attribute)
            ).longValue();
        }

        if (!ServiceHelper.doesVariableExist(aggregatingEntity, CurrTrans.class) &&
                ServiceHelper.doesVariableExist(attribute, CurrTrans.class)) {
            return jdbcTemplate.queryForObject(
                    COUNT_DIFF_AGGREGATE_CAT_AND_ATTR_CT(filtrationJoinQuery, filtrationQuery, aggregatingEntity, attribute),
                    Integer.class,
                    startDate, endDate,
                    inputData.get(aggregatingEntity), inputData.get(attribute)
            ).longValue();
        }

        if (ServiceHelper.doesVariableExist(aggregatingEntity, CurrTrans.class) &&
                !ServiceHelper.doesVariableExist(attribute, CurrTrans.class)) {
            return jdbcTemplate.queryForObject(
                    COUNT_DIFF_AGGREGATE_CT_AND_ATTR_CAT(filtrationJoinQuery, filtrationQuery, aggregatingEntity, attribute),
                    Integer.class,
                    startDate, endDate,
                    inputData.get(aggregatingEntity), inputData.get(attribute)
            ).longValue();
        }

        if (!ServiceHelper.doesVariableExist(aggregatingEntity, CurrTrans.class) &&
                !ServiceHelper.doesVariableExist(attribute, CurrTrans.class)) {
            return jdbcTemplate.queryForObject(
                    COUNT_DIFF_AGGREGATE_CAT_AND_ATTR_CAT(filtrationJoinQuery, filtrationQuery, aggregatingEntity, attribute),
                    Integer.class,
                    startDate, endDate,
                    inputData.get(aggregatingEntity), inputData.get(attribute)
            ).longValue();
        }
//        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Long> queryCount = cb.createQuery(Long.class);
//        Root<CurrTrans> root = queryCount.from(CurrTrans.class);
//
//        Predicate attributeCond = cb.equal(root.get(attribute), inputData.get(attribute));
//        Predicate aggEntity = cb.notEqual(root.get(aggregatingEntity), inputData.get(aggregatingEntity));
//        Predicate dateCondition = cb.between(root.get("sysdate"), startDate, endDate);
//
//        Predicate finalConditon = cb.and(attributeCond, aggEntity, dateCondition);
//        queryCount.select(cb.count(root)).where(finalConditon);
//
//        return entityManager.createQuery(queryCount).getSingleResult().intValue();
        return 0;
    }

    @Override
    public long limitCountAmountMatched(String aggregatingEntity, String attribute, String startDate, String endDate, Map<String, String> inputData, String filtrationJoinQuery, String filtrationQuery) {
        if (ServiceHelper.doesVariableExist(aggregatingEntity, CurrTrans.class) &&
                ServiceHelper.doesVariableExist(attribute, CurrTrans.class)) {
            return jdbcTemplate.queryForObject(
                    COUNT_AMOUNT_MATCH_AGGREGATE_CT_AND_ATTR_CT(filtrationJoinQuery, filtrationQuery, aggregatingEntity, attribute),
                    Integer.class,
                    startDate, endDate,
                    inputData.get(aggregatingEntity), inputData.get(attribute)
            ).longValue();
        }

        if (!ServiceHelper.doesVariableExist(aggregatingEntity, CurrTrans.class) &&
                ServiceHelper.doesVariableExist(attribute, CurrTrans.class)) {
            return jdbcTemplate.queryForObject(
                    COUNT_AMOUNT_MATCH_AGGREGATE_CAT_AND_ATTR_CT(filtrationJoinQuery, filtrationQuery, aggregatingEntity, attribute),
                    Integer.class,
                    startDate, endDate,
                    inputData.get(aggregatingEntity), inputData.get(attribute)
            ).longValue();
        }

        if (ServiceHelper.doesVariableExist(aggregatingEntity, CurrTrans.class) &&
                !ServiceHelper.doesVariableExist(attribute, CurrTrans.class)) {
            return jdbcTemplate.queryForObject(
                    COUNT_AMOUNT_MATCH_AGGREGATE_CT_AND_ATTR_CAT(filtrationJoinQuery, filtrationQuery, aggregatingEntity, attribute),
                    Integer.class,
                    startDate, endDate,
                    inputData.get(aggregatingEntity), inputData.get(attribute)
            ).longValue();
        }

        if (!ServiceHelper.doesVariableExist(aggregatingEntity, CurrTrans.class) &&
                !ServiceHelper.doesVariableExist(attribute, CurrTrans.class)) {
            return jdbcTemplate.queryForObject(
                    COUNT_AMOUNT_MATCH_AGGREGATE_CAT_AND_ATTR_CAT(filtrationJoinQuery, filtrationQuery, aggregatingEntity, attribute),
                    Integer.class,
                    startDate, endDate,
                    inputData.get(aggregatingEntity), inputData.get(attribute)
            ).longValue();
        }
        return 0;
    }

    @Override
    public long limitCountAmountDifferent(String aggregatingEntity, String attribute, String startDate, String endDate, Map<String, String> inputData, String filtrationJoinQuery, String filtrationQuery) {
        if (ServiceHelper.doesVariableExist(aggregatingEntity, CurrTrans.class) &&
                ServiceHelper.doesVariableExist(attribute, CurrTrans.class)) {
            return jdbcTemplate.queryForObject(
                    COUNT_AMOUNT_DIFF_AGGREGATE_CT_AND_ATTR_CT(filtrationJoinQuery, filtrationQuery, aggregatingEntity, attribute),
                    Integer.class,
                    startDate, endDate,
                    inputData.get(aggregatingEntity), inputData.get(attribute)
            ).longValue();
        }

        if (!ServiceHelper.doesVariableExist(aggregatingEntity, CurrTrans.class) &&
                ServiceHelper.doesVariableExist(attribute, CurrTrans.class)) {
            return jdbcTemplate.queryForObject(
                    COUNT_AMOUNT_DIFF_AGGREGATE_CAT_AND_ATTR_CT(filtrationJoinQuery, filtrationQuery, aggregatingEntity, attribute),
                    Integer.class,
                    startDate, endDate,
                    inputData.get(aggregatingEntity), inputData.get(attribute)
            ).longValue();
        }

        if (ServiceHelper.doesVariableExist(aggregatingEntity, CurrTrans.class) &&
                !ServiceHelper.doesVariableExist(attribute, CurrTrans.class)) {
            return jdbcTemplate.queryForObject(
                    COUNT_AMOUNT_DIFF_AGGREGATE_CT_AND_ATTR_CAT(filtrationJoinQuery, filtrationQuery, aggregatingEntity, attribute),
                    Integer.class,
                    startDate, endDate,
                    inputData.get(aggregatingEntity), inputData.get(attribute)
            ).longValue();
        }

        if (!ServiceHelper.doesVariableExist(aggregatingEntity, CurrTrans.class) &&
                !ServiceHelper.doesVariableExist(attribute, CurrTrans.class)) {
            return jdbcTemplate.queryForObject(
                    COUNT_AMOUNT_DIFF_AGGREGATE_CAT_AND_ATTR_CAT(filtrationJoinQuery, filtrationQuery, aggregatingEntity, attribute),
                    Integer.class,
                    startDate, endDate,
                    inputData.get(aggregatingEntity), inputData.get(attribute)
            ).longValue();
        }
        return 0;
    }


    @Override
    public CurrTrans findByUtrnno(Long utrnno) {
        return currTransRepository.findByUtrnno(utrnno);
    }

    protected String formatAmount(String amountString) {
        // Parse the amount string to a double
        double amount = Double.parseDouble(amountString) / 100; // Divide by 100 to move decimal point

        // Create a DecimalFormat instance for IDR with the desired format
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setCurrencySymbol("Rp");
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);

        // Format the amount
        return decimalFormat.format(amount);
    }

    protected String trimValue(String value) {
        if (value == null) {
            return null;
        }

        return value.trim();
    }
}


//    public String findTransFieldByHistory(String historyBy, String historyValue, int depth, String transField) {
//        log.info("Prepared sql Statement [ entityName :  " + historyBy + "," + " historyBy : " + historyValue
//                + "," + " offset : " + depth + "," + " transField : " + transField + "]");
//        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Object> query = cb.createQuery();
//        Root<CurrTrans> root = query.from(CurrTrans.class);
//
//        Path<Object> terminalIdPath = root.get(transField);
//        Expression<Timestamp> sysdate = cb.function("TO_TIMESTAMP", Timestamp.class, root.get("sysdate"), cb.literal("YYYY-MM-DD HH24:MI:SS.SSS"));
//        query.select(terminalIdPath)
//                .where(cb.equal(root.get(historyBy), cb.parameter(String.class, "historyBy")))
//                .orderBy(cb.desc(root.get("sysdate")));
//        String originalString = entityManager.createQuery(query).setParameter("historyBy", historyValue).setMaxResults(1).setFirstResult(depth - 1).getResultList().toString();
//        return "'" + originalString.substring(1, originalString.length() - 1) + "'";
//
//    }