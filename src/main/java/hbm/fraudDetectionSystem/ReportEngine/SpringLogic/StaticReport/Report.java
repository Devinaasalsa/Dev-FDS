package hbm.fraudDetectionSystem.ReportEngine.SpringLogic.StaticReport;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Report implements Serializable {

    private Integer reportId;

    private String reportDesc;

    private String fromDate;

    private String toDate;

    private List<Object> data = new ArrayList<>();
}
