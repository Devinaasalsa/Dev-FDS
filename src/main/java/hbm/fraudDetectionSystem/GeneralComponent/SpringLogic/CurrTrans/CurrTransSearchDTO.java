package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CurrTransSearchDTO {
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
    private String ttime;
    private String udate;
    private String sysdate;
    private String terminalId;
    private String terminalAddress;
    private Integer fraudFlags = 0;
    private Boolean isAlerted = false;
    private String cifId;
    private Integer ruleTrigger = 0;
}
