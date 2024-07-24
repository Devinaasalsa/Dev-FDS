package hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_FRAUD_LIST_TYPE")
public class FraudListType {
    @Id
    @SequenceGenerator(name = "fraudlist_type_seq", sequenceName = "fraudlist_type_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY,generator = "fraudlist_type_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int typeId;
    private String entityType;
}
