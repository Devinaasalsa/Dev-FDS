package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransactionActivity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Immutable
@Subselect(
        "select * from transaction_activity"
)
public class TransactionActivity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "converted_date")
    @Temporal(TemporalType.DATE)
    private Date convertedDate;

    @Column(name = "approve_total")
    private Integer approveTotal;

    @Column(name = "declined_total")
    private Integer declinedTotal;
}
