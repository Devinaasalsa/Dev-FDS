package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Role;


import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Operation.Operation;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.RoleExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.RoleNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RoleService {
    Role addRole(String roleName, Set<Operation> operations) throws RoleNotFoundException, RoleExistException;
    List<Role> getRoles();
    Role updateRole(String currentRoleName, String newRoleName, Set<Operation> operations) throws RoleNotFoundException, RoleExistException;
    void deleteRole(long id);
    Role findRoleByRoleName(String roleName);
    List<Role> search(Map<String, Object> reqBody);
}
