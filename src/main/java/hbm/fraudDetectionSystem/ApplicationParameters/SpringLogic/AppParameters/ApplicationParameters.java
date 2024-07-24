package hbm.fraudDetectionSystem.ApplicationParameters.SpringLogic.AppParameters;

import hbm.fraudDetectionSystem.ApplicationParameters.SpringLogic.paramType.ParamType;
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
@Table(name = "T_APPLICATION_PARAMETERS")
public class ApplicationParameters {
    @Id
    @SequenceGenerator(
            name = "application_parameters_seq",
            sequenceName = "application_parameters_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "application_parameters_seq")
    private Long id;
    private String paramName;
    private String value;
    private String description;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "param_type", referencedColumnName = "paramId")
    private ParamType paramType;
}
