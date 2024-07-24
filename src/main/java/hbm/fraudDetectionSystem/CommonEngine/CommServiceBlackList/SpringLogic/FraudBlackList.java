package hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.SpringLogic;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.User;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;



@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_FRAUD_BLACK_LIST")
public class FraudBlackList implements Serializable{
    @Id
    @SequenceGenerator(name = "black_list_seq", sequenceName = "black_list_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "black_list_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String entityType;
    private String value;
    private Timestamp dateIn;
    private Timestamp dateOut;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_group_id", referencedColumnName = "id")
    private UserGroup userGroup;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    private User initiator;
    private String reason;
}
