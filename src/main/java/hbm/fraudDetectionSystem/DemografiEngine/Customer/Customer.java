package hbm.fraudDetectionSystem.DemografiEngine.Customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "T_CUSTOMER_INFO")
public class Customer implements Serializable {
    @Id
    @SequenceGenerator(name = "customer_seq", sequenceName = "customer_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "customer_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String custNumber;
    private String custCategory;
    private String custRelation;
    private Integer resident;
    private Integer nationality;
    private String creditRating;
    private String moneyLaundryRisk;
    private String moneyLaundryReason;
    private String entityType;
//    @OneToMany(
//            cascade = CascadeType.ALL
//    )
//    @JoinColumn(name = "cust_number", referencedColumnName = "custNumber")
//    private List<Card>cardList;
//    @OneToMany(
//            cascade = CascadeType.ALL
//    )
//    @JoinColumn(name = "cust_number", referencedColumnName = "custNumber")
//    private Set<Account> accountList;

//    @OneToOne(mappedBy = "customer")
//    private Person person;
}
