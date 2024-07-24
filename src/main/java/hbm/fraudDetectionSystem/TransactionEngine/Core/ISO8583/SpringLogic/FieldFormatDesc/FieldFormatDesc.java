package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldFormatDesc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@JsonIgnoreProperties(value = {"description"})
@Getter
@Setter
@Entity
@Table(name = "field_format_desc")
public class FieldFormatDesc {
    @Id
    @SequenceGenerator(name = "field_format_Sequence",sequenceName = "field_format_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "field_format_Sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long formatId;
    private String formatType;
    @Transient
    private String description;
}
