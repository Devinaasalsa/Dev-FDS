package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransactionStatus;

import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionStatus {
    private String fraudCategory;

    private Integer totalCount;
}
