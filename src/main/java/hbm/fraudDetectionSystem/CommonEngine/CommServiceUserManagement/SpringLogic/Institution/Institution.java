package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Institution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_INSTITUTION")
public class Institution {
    @Id
    @SequenceGenerator(name = "institution_sequence", sequenceName = "institution_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "institution_sequence")
    private Long id;

    @Column(nullable = false)
    String institutionName;
    String description;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "institution", fetch = FetchType.LAZY)
//    @JsonIgnore
//    private List<User> users;
}
