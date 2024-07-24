package hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.SpringLogic;

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
@Table(name = "T_FRAUD_WHITE_LIST")
public class FraudWhiteList implements Serializable{
    @Id
    @SequenceGenerator(name = "white_list_seq", sequenceName = "white_list_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "white_list_seq")
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
