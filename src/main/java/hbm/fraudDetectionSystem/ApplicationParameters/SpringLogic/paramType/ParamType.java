package hbm.fraudDetectionSystem.ApplicationParameters.SpringLogic.paramType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_PARAMETERS_TYPE")
public class ParamType {
    @Id
    @SequenceGenerator(name = "param_type_seq", sequenceName = "param_type_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "param_type_seq")
    private Long paramId;
    private String paramType;
}
