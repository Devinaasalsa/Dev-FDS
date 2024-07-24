package hbm.fraudDetectionSystem.ReportEngine.SpringLogic.JasperReport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_JASPER_REPORT")
public class JReport implements Serializable {
    @Id
    @SequenceGenerator(name = "jReport_seq", sequenceName = "jReport_seq", allocationSize = 1, initialValue = 8)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "jReport_seq")
    private Long reportId;
    private String reportName;

    @JsonIgnore
    private byte[] jrReport;
}
