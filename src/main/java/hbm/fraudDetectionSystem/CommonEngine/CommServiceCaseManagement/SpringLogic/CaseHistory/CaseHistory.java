package hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.SpringLogic.CaseHistory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_ALERT_INV_HISTORY")
public class CaseHistory {
    @Id
    @SequenceGenerator(name = "alert_inv_history_seq", sequenceName = "alert_inv_history_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "alert_inv_history_seq")
    private Long id;
    private String actionType;
    private Timestamp actionDate;
    private String initiator;
    private String info;
    private Long caseId;
}
