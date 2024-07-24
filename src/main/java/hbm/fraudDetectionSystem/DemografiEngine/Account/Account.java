package hbm.fraudDetectionSystem.DemografiEngine.Account;

import hbm.fraudDetectionSystem.DemografiEngine.Card.Card;
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
@Table(name = "T_ACCOUNT_INFO")
public class Account implements Serializable {
    @Id
    @SequenceGenerator(
            name = "acctount_sequence",
            sequenceName = "acctount_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "acctount_sequence"
    )
    private long id;
    private String accountNumber;
    private String currency;
    private String accountType;
    private String accountStatus;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "card_id", referencedColumnName = "id")
    private Card card;
}
