package hbm.fraudDetectionSystem.DemografiEngine.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountNumber(String accountNumber);

    Set<Account> findAllByCard_CardNumberIn(List<String> cardList);

    Set<Account> findAllByCard_CardNumber(String cardNumber);
}
