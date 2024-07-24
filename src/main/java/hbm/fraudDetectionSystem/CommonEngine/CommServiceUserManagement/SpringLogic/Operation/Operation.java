package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Operation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Privilege.Privilege;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Role.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "t_operation")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Operation {
    @Id
    @SequenceGenerator(name = "operation_seq", sequenceName = "operation_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "operation_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long opId;

    private String opName;

    private String description;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "op_privilege",
            joinColumns = @JoinColumn(name = "opId"),
            inverseJoinColumns = @JoinColumn(name = "privilegeId")
    )
    private Set<Privilege> privileges = new HashSet<>();
}
