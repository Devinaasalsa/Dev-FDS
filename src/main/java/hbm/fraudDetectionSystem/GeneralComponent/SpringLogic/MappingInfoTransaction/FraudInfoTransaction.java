package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.MappingInfoTransaction;

import hbm.fraudDetectionSystem.DemografiEngine.Account.Account;
import hbm.fraudDetectionSystem.DemografiEngine.Card.Card;
import hbm.fraudDetectionSystem.DemografiEngine.Customer.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "T_FRAUD_INFO_TRANSACTION")
public class FraudInfoTransaction implements Serializable {
    @Id
    @SequenceGenerator(name = "info_trans_seq", sequenceName = "info_trans_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "info_trans_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String refnum;
    private String utrnno;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "customer_number", referencedColumnName = "id")
    private Customer customerNumber;

    @ManyToMany(cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
    @JoinTable(
            name = "card_detailsInfo_map",
            joinColumns = @JoinColumn(
                name = "id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(name = "card_number",referencedColumnName = "cardNumber")
    )
    private List<Card>cardList;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "account_detailsInfo_map",
            joinColumns = @JoinColumn(
                    name = "id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(name = "account_number",referencedColumnName = "accountNumber")
    )
    private Set<Account> accountList;

    public void addCard(Card card){
        if (card != null)cardList = new ArrayList<>();
        cardList.add(card);
    }

    public void addAccount(Account account){
        if (account != null)accountList = new HashSet<>();
        accountList.add(account);
    }
}
