package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByRoleName(String roleName);

    @Query(
            nativeQuery = true,
            value = "select * from t_role where role_id != 20 order by role_id asc"
    )
    List<Role> findByOrderByRoleIdAsc();
}
