package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.MappingInfoTransaction;

import hbm.fraudDetectionSystem.DemografiEngine.Account.Account;
import hbm.fraudDetectionSystem.DemografiEngine.Account.AccountService;
import hbm.fraudDetectionSystem.DemografiEngine.Card.Card;
import hbm.fraudDetectionSystem.DemografiEngine.Card.CardService;
import hbm.fraudDetectionSystem.DemografiEngine.Customer.Customer;
import hbm.fraudDetectionSystem.DemografiEngine.Customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FraudInfoTransServiceImpl implements FraudInfoTransService {
    private FraudInfoTransRepository repository;
    private CustomerService customerService;
    private CardService cardService;
    private AccountService accountService;

    @Autowired
    public FraudInfoTransServiceImpl(FraudInfoTransRepository repository, CustomerService customerService, CardService cardService, AccountService accountService) {
        this.repository = repository;
        this.customerService = customerService;
        this.cardService = cardService;
        this.accountService = accountService;
    }

    @Override
    public FraudInfoTransaction addTransInfoByEntity(String refnum, String utrnno, String custNumber, String cardNumber, String accountNumber) {
        FraudInfoTransaction infoTransaction = new FraudInfoTransaction();
        if (custNumber != null) {
            infoTransaction.setCustomerNumber(findCustomerByCustNumber(custNumber));

            List<Card> cardList = cardService.findCardByCustomerNum(custNumber);
            infoTransaction.setCardList(cardList);

            infoTransaction.setAccountList(accountService.findAccountByCardNumber(
                    cardList
                            .stream()
                            .map(Card::getCardNumber)
                            .collect(Collectors.toList())
            ));

            infoTransaction.setRefnum(refnum);
            infoTransaction.setUtrnno(utrnno);
            repository.save(infoTransaction);
        } else if (cardNumber != null) {
            Card getCard = findCardByCardNumber(cardNumber);
            if (getCard != null) {
                infoTransaction.addCard(getCard);
                infoTransaction.setAccountList(
                        accountService.findAccountByCardNumber(getCard.getCardNumber())
                );
                infoTransaction.setUtrnno(utrnno);
                infoTransaction.setRefnum(refnum);
                repository.save(infoTransaction);
            }
        } else if (accountNumber != null) {
            Account getAccount = findAccountByAccountNumber(accountNumber);

            if (getAccount != null) {
                infoTransaction.addAccount(getAccount);
                infoTransaction.setRefnum(refnum);
                infoTransaction.setUtrnno(utrnno);
                repository.save(infoTransaction);
            }
        }

        return infoTransaction;
    }

    @Override
    public FraudInfoTransaction findTransInfoByEntity(String utrnno, String refnum) {
        return repository.findByUtrnnoAndRefnum(utrnno, refnum);
    }

    private Account findAccountByAccountNumber(String accountNumber) {
        return accountService.findAccountByAccountNumber(accountNumber);
    }

    private Card findCardByCardNumber(String cardNumber) {
        return cardService.findCardByCardNumber(cardNumber);
    }

    private Customer findCustomerByCustNumber(String custNumber) {
        return customerService.findByCustomerId(custNumber);
    }
}
