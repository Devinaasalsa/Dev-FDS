package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.MappingInfoTransaction;

import java.util.List;

public interface FraudInfoTransService {
    FraudInfoTransaction addTransInfoByEntity(String refnum, String utrnno, String custNumber, String cardNumber, String accountNumber);

    FraudInfoTransaction findTransInfoByEntity(String utrnno, String refnum);
}
