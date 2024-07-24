package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Role;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Operation.Operation;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Operation.OperationService;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.constant.RoleConstant;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.RoleExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.RoleNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {
    @PersistenceContext
    private EntityManager em;
    private final RoleRepository roleRepository;
    protected final OperationService operationService;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, OperationService operationService) {
        this.roleRepository = roleRepository;
        this.operationService = operationService;
    }

    @Override
    public Role addRole(String roleName, Set<Operation> operations) throws RoleNotFoundException, RoleExistException {
        validateNewRole(StringUtils.EMPTY, roleName);
        Role role = new Role();
        role.setRoleName(roleName);
        role.setOperations(getOperations(operations));

        roleRepository.save(role);
        return role;
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findByOrderByRoleIdAsc();
    }

    @Override
    public Role updateRole(String currentRoleName, String newRoleName, Set<Operation> operations) throws RoleNotFoundException, RoleExistException {
        Role currentRole = validateNewRole(currentRoleName, newRoleName);
        assert currentRole != null;
        currentRole.setRoleName(newRoleName);
        currentRole.setOperations(getOperations(operations));
        roleRepository.save(currentRole);
        return currentRole;
    }

    @Override
    public void deleteRole(long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public Role findRoleByRoleName(String roleName) {
        return roleRepository.findRoleByRoleName(roleName);
    }

    @Override
    public List<Role> search(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<Role> query = cb.createQuery(Role.class);
        Root<Role> root = query.from(Role.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                        if (!value.toString().isEmpty()) {
                            predicates.add(cb.equal(root.get(key), value));
                        }
                });

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<Role> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    private Role validateNewRole(String currentRoleName, String newRoleName) throws RoleNotFoundException, RoleExistException {
        Role roleByNewRoleName = findRoleByRoleName(newRoleName);

        if (StringUtils.isNotBlank(currentRoleName)) {
            Role currentRole = findRoleByRoleName(currentRoleName);
            if (currentRole == null) {
                throw new RoleNotFoundException(RoleConstant.NO_ROLE_FOUND_BY_ROLE_NAME + currentRoleName);
            }
            if (roleByNewRoleName != null && !currentRole.getRoleId().equals(roleByNewRoleName.getRoleId())) {
                throw new RoleExistException(RoleConstant.ROLE_ALREADY_EXISTS);
            }

            return currentRole;
        } else {
            if (roleByNewRoleName != null) {
                throw new RoleExistException(RoleConstant.ROLE_ALREADY_EXISTS);
            }

            return null;
        }
    }

    private Set<Operation> getOperations(Set<Operation> operationList){
        List<Long> listOperations = new ArrayList<>();
        operationList.forEach(operation -> {
//            System.out.println("op id: " + operation.getOpId());
//            System.out.println("op name: " + operation.getOpName());
            listOperations.add(operation.getOpId());
        });
        return operationService.getOperationByListId(listOperations);
    }
}
