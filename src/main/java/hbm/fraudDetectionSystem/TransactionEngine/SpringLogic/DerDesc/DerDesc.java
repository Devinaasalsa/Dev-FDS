package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.DerDesc;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "der_desc")
public class DerDesc {
    @Id
    @SequenceGenerator(name = "der_desc_Sequence",sequenceName = "der_desc_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "der_desc_Sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long derId;
    private String notation;
    @Column(length = 65535)
    private String Interpretation;
}
