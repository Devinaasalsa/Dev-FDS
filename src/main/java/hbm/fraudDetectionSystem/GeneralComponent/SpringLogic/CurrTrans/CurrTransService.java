package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans;


import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrAddtTrans.CurrAddtTrans;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface CurrTransService {
    List<CurrTrans> findAllTransaction();

    List<CurrTrans> findAllByHpan(String hpan);

    List<CurrTrans> findAllByHpanCustIdAcct1(String hpan, String custId, String acct1, String alertDate);

    CurrTrans assignFraudFlag(long utrnno,int fraudFlag);

    String findTransFieldByHistory(String entityName, String historyBy, int offset, String transField);

    long limitCountMatched(String aggregatingEntity, String attribute, String startDateTime, String endDateTime, Map<String, String> inputData, String filtrationJoinQuery, String filtrationQuery);

    long limitCountDifferent(String aggregatingEntity, String attribute, String startDate, String endDate, Map<String, String> inputData, String filtrationJoinQuery, String filtrationQuery);

    long limitCountAmountMatched(String aggregatingEntity, String attribute, String startDate, String endDate, Map<String, String> inputData, String filtrationJoinQuery, String filtrationQuery);

    long limitCountAmountDifferent(String aggregatingEntity, String attribute, String startDate, String endDate, Map<String, String> inputData, String filtrationJoinQuery, String filtrationQuery);

    CurrTrans findByUtrnno(Long utrnno);


    //TODO::Pleaselah Merchant Data
   // String findMerchantByOffset(String merchant, int offset);

    //TODO::Pleaselah Customer Data
    //String findCustomerByOffset(String customer, int offset);

    CurrTrans saveTransaction(Map<String, String> preparedData, List<CurrAddtTrans> preparedAddtData, boolean cvtAmount) throws ParseException;

    void updateAsAlertedByUtrnno(Long utrnno);

    List<CurrTrans> searchTransaction(Map<String, Object> reqBody);
}
