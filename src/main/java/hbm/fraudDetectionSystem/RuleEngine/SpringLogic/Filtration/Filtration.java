package hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Filtration;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "T_AGGREGATE_FILTRATION")
@IdClass(Filtration.IdClass.class)
public class Filtration implements Serializable{
    @Id
    @SequenceGenerator(name = "filtration_seq", sequenceName = "filtration_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "filtration_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long filtrationId;
    @Id
    private String conditionId;
    private String operator;
    private String operatorDetails;
    private String attribute;
    private String value;
    private String minRange;
    private String maxRange;

    @Data
    static class IdClass implements Serializable{
        private Long filtrationId;
        private String conditionId;
    }
}