package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransTypeDesc;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "trans_type_desc")
public class TransTypeDesc {
    @Id
    @SequenceGenerator(name = "trans_type_desc_Sequence",sequenceName = "trans_type_desc_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "trans_type_desc_Sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long typeId;
    private String description;
    private String code;
}
