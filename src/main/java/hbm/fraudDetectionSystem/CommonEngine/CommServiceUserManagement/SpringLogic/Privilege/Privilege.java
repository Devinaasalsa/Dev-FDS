package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Privilege;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "t_privilege")
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"privilegeId"})
@Getter
@Setter
public class Privilege {
    @Id
    @SequenceGenerator(name = "privilege_seq", sequenceName = "privilege_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "privilege_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long privilegeId;
    private String description;
}
