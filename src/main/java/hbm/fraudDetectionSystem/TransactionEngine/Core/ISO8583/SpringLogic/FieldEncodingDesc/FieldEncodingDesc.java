package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldEncodingDesc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@JsonIgnoreProperties(value = {"description"})
@Getter
@Setter
@Entity
@Table(name = "FIELD_ENCODING_DESC")
public class FieldEncodingDesc {
    @Id
    @SequenceGenerator(name = "Encoding_Config_Sequence",sequenceName = "Encoding_Config_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "Encoding_Config_Sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long encodingId;
    @Column(unique = true, nullable = false)
    private String encodingType;
    @Transient
    private String description;

    @JsonIgnore
    public int getIntId() {
        return this.encodingId.intValue();
    }
}
