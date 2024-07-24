package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrAddtTrans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans.CurrTrans;
import lombok.*;

import javax.persistence.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "curr_addt_trans")
@IdClass(CurrAddtTransPK.class)
public class CurrAddtTrans {

    @Id
    @ManyToOne
    @JoinColumn(name = "utrnno")
    private CurrTrans utrnno;

    @Id
    @Column(name = "attr")
    private String attr;

    @Column(name = "description")
    private String description;

    @Column(name = "value")
    private String value;
}

