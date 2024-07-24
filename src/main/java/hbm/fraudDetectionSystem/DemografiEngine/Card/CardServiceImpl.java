package hbm.fraudDetectionSystem.DemografiEngine.Card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardServiceImpl implements CardService{
    private CardRepository repository;

    @Autowired
    public CardServiceImpl(CardRepository repository) {
        this.repository = repository;
    }

    @Override
    public Card findCardByCardNumber(String cardNumber) {
        return repository.findByCardNumber(cardNumber);
    }

    @Override
    public List<Card> findCardByCustomerNum(String customerNum) {
        return repository.findAllByCustomerCustNumber(customerNum);
    }
}
