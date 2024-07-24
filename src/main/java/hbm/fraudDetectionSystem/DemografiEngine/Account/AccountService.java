package hbm.fraudDetectionSystem.DemografiEngine.Account;

import java.util.List;
import java.util.Set;

public interface AccountService {
    Account findAccountByAccountNumber(String accountNumber);
    Set<Account> findAccountByCardNumber(List<String> cardNumber);
    Set<Account> findAccountByCardNumber(String cardNumber);
}
