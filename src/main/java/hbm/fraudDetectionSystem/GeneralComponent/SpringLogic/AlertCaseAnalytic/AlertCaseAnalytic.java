package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.AlertCaseAnalytic;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@Immutable
@Subselect(
        "select * from alert_case_analytic"
)
public class AlertCaseAnalytic {
    @Id
    @Column(name = "id")
    private Long id;

    private Long notClassified;

    private Long negative;

    private Long suspicious;

    private Long positive;
}
