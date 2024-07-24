package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.domain;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Operation.Operation;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Privilege.Privilege;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Role.Role;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.constant.OperationURIConstant.LIST_URI_BY_OP;


public class UserPrincipal implements UserDetails {
    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getGrantedAuthorities(getOperationPrivilege(user.role));
    }

    public List<String> getOperationPrivilege(Role role) {
        List<String> operationPrivilege = new ArrayList<>();

        operationPrivilege.add(role.getRoleName());
        for (Operation operation : role.getOperations()) {
            operationPrivilege.add(
                    new StringBuilder()
                            .append(operation.getOpId())
                            .append("_OP_")
                            .append(operation.getOpName())
                            .append("[")
                            .append(LIST_URI_BY_OP.get(operation.getOpName()))
                            .append("]")
                            .toString()
            );

            for (Privilege privilege : operation.getPrivileges()) {
                operationPrivilege.add(
                        new StringBuilder()
                                .append(operation.getOpId())
                                .append("_PRIV_")
                                .append(privilege.getDescription())
                                .toString()
                );
            }
        }


        return operationPrivilege;
    }

    public List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.user.isNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.isActive();
    }
}
