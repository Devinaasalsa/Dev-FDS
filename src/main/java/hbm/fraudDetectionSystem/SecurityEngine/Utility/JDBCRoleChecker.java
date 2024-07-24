package hbm.fraudDetectionSystem.SecurityEngine.Utility;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Objects;

import static hbm.fraudDetectionSystem.SecurityEngine.Constant.SecurityConstant.PUBLIC_URLS;


@Configuration
public class JDBCRoleChecker {
    public boolean check(Authentication authentication, HttpServletRequest request) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String comparisonData = authorities.toString();

        if (Objects.equals(request.getRequestURI(), PUBLIC_URLS)) {
            return true;
        }

        if (comparisonData.contains("/" + request.getRequestURI().split("/")[1])) {
            if (comparisonData.contains(request.getMethod())){
                return true;
            }
        }

        return false;
    }
}
