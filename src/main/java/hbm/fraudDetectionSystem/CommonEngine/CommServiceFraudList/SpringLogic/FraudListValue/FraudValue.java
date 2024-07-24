package hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListValue;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudList.FraudList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "T_FRAUD_LIST_VALUE")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FraudValue implements Serializable {
    @Id
    @SequenceGenerator(name = "fraud_list_value_seq", sequenceName = "fraud_list_value_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "fraud_list_value_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "list_id", referencedColumnName = "listId")
    private FraudList listId;
    private String value;
    private String author;
    private Timestamp creationDate;

}
