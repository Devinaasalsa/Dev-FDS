package hbm.fraudDetectionSystem.DemografiEngine.Card;

import java.util.List;

public interface CardService {
    Card findCardByCardNumber(String cardNumber);
    List<Card> findCardByCustomerNum(String customerNum);
}
