package hbm.fraudDetectionSystem.DemografiEngine.Card;

import hbm.fraudDetectionSystem.DemografiEngine.Account.Account;
import hbm.fraudDetectionSystem.DemografiEngine.Customer.Customer;
import hbm.fraudDetectionSystem.DemografiEngine.Person.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "T_CARD_INFO")
public class Card implements Serializable {
    @Id
    @SequenceGenerator(name = "card_seq", sequenceName = "card_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "card_seq")
    private Long id;
    private String cardNumber;
    private String cardMask;
    private String cardId;
    private Timestamp cardIssDate;
    private Timestamp cardStartDate;
    private Timestamp expirationDate;
    private int instanceId;
    private int precedingInstanceId;
    private int sequentialNumber;
    private String cardStatus;
    private String cardState;
    private String category;
    private int pvv;
    private int pinOffset;
//    @Type(type = "yes_no")
    private boolean pinUpdateFlag;
    private int cardTypeId;
    private String prevCardNumber;
    private String prevCardId;
    private String agentNumber;
    private String agentName;
    private String productNumber;
    private String productName;
    private String companyName;
    private String serviceCode;
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

}
