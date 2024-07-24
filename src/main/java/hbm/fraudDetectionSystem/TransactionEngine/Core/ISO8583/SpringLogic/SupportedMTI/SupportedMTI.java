package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.SupportedMTI;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "SUPPORTED_MTI")
public class SupportedMTI {
    @Id
    @SequenceGenerator(name = "Supported_MTI_Sequence",sequenceName = "Supported_MTI_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "Supported_MTI_Sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(unique = true, nullable = false)
    private String value;
    @Type(type = "yes_no")
    private Boolean isResponse;
    @Type(type = "yes_no")
    private Boolean isReversal;
    @Type(type = "yes_no")
    private Boolean isNetwork;
    private String respValue;
}
