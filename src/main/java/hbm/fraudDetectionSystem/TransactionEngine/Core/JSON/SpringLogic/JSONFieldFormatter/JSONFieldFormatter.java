package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldFormatter;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "JSON_FIELD_FORMATTER")
public class JSONFieldFormatter {
    @Id
    @SequenceGenerator(name = "json_fFormatter_Sequence", sequenceName = "json_fFormatter_Sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "json_fFormatter_Sequence")
    private Long formatterId;
    private String format;
    private Integer srcData; //Valid value are 0: Request 1: Response
}
