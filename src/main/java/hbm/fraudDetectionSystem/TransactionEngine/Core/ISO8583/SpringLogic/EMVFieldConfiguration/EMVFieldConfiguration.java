package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.EMVFieldConfiguration;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "EMV_FIELD_CONFIGURATION")
public class EMVFieldConfiguration {
    @Id
    @SequenceGenerator(name = "emv_field_sequence", sequenceName = "emv_field_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "emv_field_sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(unique = true, nullable = false)
    private String emvTagId;
    private String description;
}
