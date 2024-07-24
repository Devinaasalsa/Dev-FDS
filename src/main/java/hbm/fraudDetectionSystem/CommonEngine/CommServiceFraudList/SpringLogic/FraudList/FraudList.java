package hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudList;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListType.FraudListType;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_FRAUD_LIST")
public class FraudList implements Serializable {
    @Id
    @SequenceGenerator(name = "fraud_list_seq", sequenceName = "fraud_list_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "fraud_list_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long listId;
    private String listName;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_group_id",referencedColumnName = "id")
    private UserGroup userGroup;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "entity_type_id", referencedColumnName = "typeId")
    private FraudListType entityType;
}
