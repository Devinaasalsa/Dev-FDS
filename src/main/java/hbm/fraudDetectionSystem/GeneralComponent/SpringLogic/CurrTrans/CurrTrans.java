package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrAddtTrans.CurrAddtTrans;
import hbm.fraudDetectionSystem.ReactionEngine.SpringLogic.ExReaction.ReactionContainer;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleTriggered.RuleTriggered;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "CURR_TRANS")
public class CurrTrans implements Serializable {
    @Id
    private Long utrnno;
    private String pid;
    private String acct1;
    private String acct2;
    private String acqInstitCode;
    private String issInstitCode;
    private String desInstitCode;
    private String amount;
    private String cvtAmount;
    private String feeAmount;
    private String acctBalance;
    @JsonIgnore
//    @Transient
    private String captDate;
    private String currency;
    private String hpan;
    private String transType;
    private String transTypeDesc;
    private String posDataCode;
    private String prcCode;
    private String refNum;
    private String extRespCode;
    private String respCode;
    private String respCodeDesc;
    private String stan;
    private String merchantType;
    @JsonIgnore
//    @Transient
    private String ttime;
    @JsonIgnore
//    @Transient
    private String udate;

    @Column(name = "\"sysdate\"")
    private String sysdate;
    private String terminalId;
    private String terminalAddress;
    private Integer fraudFlags = 0;
//    @Column(columnDefinition = "boolean default false")
    @Type(type = "yes_no")
    private Boolean isAlerted = false;
    private String cifId;
    @Type(type = "yes_no")
    @Column(columnDefinition = "Character(1) default 'N'")
    private Boolean isReversal;

    @Transient
    @JsonIgnore
    private List<CurrAddtTrans> addtData;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "utrnno", referencedColumnName = "utrnno")
    @JsonIgnore
    private Set<RuleTriggered> ruleInfo = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "utrnno", referencedColumnName = "utrnno")
    @JsonIgnore
    private Set<ReactionContainer> exReactions = new HashSet<>();

    @Transient
    private Long ruleTrigger = 0L;

    public CurrTrans(String refNum, String stan) {
        this.refNum = refNum;
        this.stan = stan;
    }

    public CurrTrans(Long utrnno, String pid, String acct1, String acct2, String acqInstitCode, String issInstitCode, String desInstitCode, String amount, String cvtAmount, String feeAmount, String acctBalance, String captDate, String currency, String hpan, String transType, String transTypeDesc, String posDataCode, String prcCode, String refNum, String extRespCode, String respCode, String respCodeDesc, String stan, String merchantType, String ttime, String udate, String sysdate, String terminalId, String terminalAddress, Integer fraudFlags, Boolean isAlerted, Boolean isReversal, String cifId, Long ruleTrigger) {
        this.utrnno = utrnno;
        this.pid = pid;
        this.acct1 = acct1;
        this.acct2 = acct2;
        this.acqInstitCode = acqInstitCode;
        this.issInstitCode = issInstitCode;
        this.desInstitCode = desInstitCode;
        this.amount = amount;
        this.cvtAmount = cvtAmount;
        this.feeAmount = feeAmount;
        this.acctBalance = acctBalance;
        this.captDate = captDate;
        this.currency = currency;
        this.hpan = hpan;
        this.transType = transType;
        this.transTypeDesc = transTypeDesc;
        this.posDataCode = posDataCode;
        this.prcCode = prcCode;
        this.refNum = refNum;
        this.extRespCode = extRespCode;
        this.respCode = respCode;
        this.respCodeDesc = respCodeDesc;
        this.stan = stan;
        this.merchantType = merchantType;
        this.ttime = ttime;
        this.udate = udate;
        this.sysdate = sysdate;
        this.terminalId = terminalId;
        this.terminalAddress = terminalAddress;
        this.fraudFlags = fraudFlags;
        this.isAlerted = isAlerted;
        this.isReversal = isReversal;
        this.cifId = cifId;
        this.ruleTrigger = ruleTrigger;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String className = "CT";
        for (Field f : getClass().getDeclaredFields()) {
            Object value;
            try {
                value = f.get(this);
                if (value != null) {
                    if (value instanceof List) {
                        List<?> list = (List<?>) value;
                        int i = 1;
                        for (Object obj : list) {
                            Class<?> objClass = obj.getClass();
                            Field[] objFields = objClass.getDeclaredFields();
                            LOGWithoutTime(result, String.format("ADDT_DATA %s:", i));
                            for (Field objField : objFields) {
                                objField.setAccessible(true);
                                if (!objField.getName().equals("utrnno") && !objField.getName().equals("description")){
                                    Object objValue = objField.get(obj);
                                    StringBuilder fixName = new StringBuilder()
                                            .append("\t")
                                            .append(className)
                                            .append("_")
                                            .append(objField.getName().toUpperCase());
                                    if (objValue != null) {
                                        LOGClass(result, fixName.toString().toUpperCase(), objValue.toString(), 20);
                                    }
                                }
                            }
                            i++;
                        }
                    } else if (value instanceof Set) {
                        Set<?> list = (Set<?>) value;
                        int i = 1;
                        for (Object obj : list) {
                            if (obj.getClass() == RuleTriggered.class) {
                                Class<?> objClass = obj.getClass();
                                Field[] objFields = objClass.getDeclaredFields();
                                LOGWithoutTime(result, String.format("TRIGGERED_RULE %s:", i));
                                for (Field objField : objFields) {
                                    objField.setAccessible(true);
                                    if (objField.getName().equals("ruleId")) {
                                        Object objValue = objField.get(obj);
                                        StringBuilder fixName = new StringBuilder()
                                                .append("\t")
                                                .append(className)
                                                .append("_")
                                                .append(objField.getName().toUpperCase());
                                        if (objValue != null) {
                                            LOGClass(result, fixName.toString().toUpperCase(), objValue.toString(), 20);
                                        }
                                    }
                                }
                                i++;
                            }
                        }
                    } else {
                        if (!f.getName().equalsIgnoreCase("RULETRIGGER"))
                            LOGClass(result, className + "_" + f.getName().toUpperCase(), value.toString(), 20);
                    }
                }

            } catch (IllegalAccessException ignored) {

            }
        }
        return result.toString().replaceAll("\\n$", "");
    }
}