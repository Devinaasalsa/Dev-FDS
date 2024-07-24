package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Operation.Operation;
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
@Table(name = "T_ROLE")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Role {
    @Id
    @SequenceGenerator(name = "role_seq", sequenceName = "role_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "role_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long roleId;

    @Column(nullable = false)
    private String roleName;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_op",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "op_id")
    )
    @OrderBy("opName ASC")
    private Set<Operation> operations;
}
