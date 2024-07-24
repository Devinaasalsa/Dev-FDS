package hbm.fraudDetectionSystem.DemografiEngine.Account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AccountServiceImpl implements AccountService {
    private AccountRepository repository;

    @Autowired
    public AccountServiceImpl(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public Account findAccountByAccountNumber(String accountNumber) {
        return repository.findByAccountNumber(accountNumber);
    }

    @Override
    public Set<Account> findAccountByCardNumber(List<String> cardNumber) {
        return repository.findAllByCard_CardNumberIn(cardNumber);
    }

    @Override
    public Set<Account> findAccountByCardNumber(String cardNumber) {
        return repository.findAllByCard_CardNumber(cardNumber);
    }
}
