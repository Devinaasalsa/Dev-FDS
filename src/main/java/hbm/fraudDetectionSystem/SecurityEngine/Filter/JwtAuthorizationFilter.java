package hbm.fraudDetectionSystem.SecurityEngine.Filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration.ChannelConfigurationRepository;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.User;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.UserService;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserAudit.UserAudit;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserAudit.UserAuditService;
import hbm.fraudDetectionSystem.SecurityEngine.Constant.SecurityConstant;
import hbm.fraudDetectionSystem.SecurityEngine.Utility.JWTTokenProvider;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.dateFormatter;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private UserAudit userAudit;
    protected final JWTTokenProvider jwtTokenProvider;
    protected final UserService userService;
    protected final UserAuditService userAuditService;
    protected final ChannelConfigurationRepository channelConfigurationRepository;
    @Value("${json.baseEndpoint.path}")
    public String[] jsonBaseEndpoint;

//    protected final AllSequencesService allSequencesService;

    public JwtAuthorizationFilter(JWTTokenProvider jwtTokenProvider, UserService userService, UserAuditService userAuditService, ChannelConfigurationRepository channelConfigurationRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.userAuditService = userAuditService;
//        this.allSequencesService = allSequencesService;
        this.channelConfigurationRepository = channelConfigurationRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String baseEndpoint = this.extractBaseUrl(request.getRequestURI());

        if (!baseEndpoint.isEmpty()) {
            int count = this.channelConfigurationRepository.findDataByBaseEndpoint(baseEndpoint);
            if (count > 0) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

//        long localAuditId = AUDIT_ID = AUDIT_ID + INCREMENT_TRANS_VAL;
//        System.out.println(localAuditId);
//        updateAuditIdSeq(localAuditId);

        userAudit = new UserAudit(dateFormatter(), System.currentTimeMillis());

        try {
            if (request.getMethod().equalsIgnoreCase(SecurityConstant.OPTIONS_HTTP_METHOD)) {
                response.setStatus(OK.value());
            } else {
                String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                if (authorizationHeader == null || !authorizationHeader.startsWith(SecurityConstant.TOKEN_HEADER)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                try {
                    String token = authorizationHeader.substring(SecurityConstant.TOKEN_HEADER.length());
                    String username = jwtTokenProvider.getSubject(token);
                    User user = userService.findByUsername(username);
                    if (user != null) {
                        userAudit.setUser(user);
                        if (jwtTokenProvider.isTokenValid(username, token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                            List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
                            Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        } else {
                            SecurityContextHolder.clearContext();
                        }
                    } else {
                        SecurityContextHolder.clearContext();
                    }
                    filterChain.doFilter(request, response);
                } catch (TokenExpiredException e) {
                    HttpStatus status = UNAUTHORIZED;
                    HttpResponse<Object> httpResponse = new HttpResponse<>(e.getMessage(), status.value(), status.getReasonPhrase().toUpperCase());

                    response.setStatus(UNAUTHORIZED.value());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), httpResponse);
                }
            }
        } finally {
            if (userAudit.getUser() != null) {
                if (userAudit.getUser().getType().getId() != 3L)
                    afterRequest(requestWrapper, responseWrapper);
            }
        }

    }

    protected String extractBaseUrl(String fullURI) {
        String[] baseUrls = this.jsonBaseEndpoint;
        for (String baseUrl : baseUrls) {
            if (fullURI.startsWith(baseUrl)) {
                return baseUrl.startsWith("/") ? baseUrl.substring(1) : baseUrl;
            }
        }
        return "";
    }

    private void afterRequest(ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper) {
        /*
            Uncomment this for get JWT from the request
            requestWrapper.getHeader("authorization");
         */

        String method = requestWrapper.getMethod();
        String url = requestWrapper.getRequestURI();
        if (method.equals("GET") || url.contains("search")) {
            return;
        }

        Long timeTaken = System.currentTimeMillis() - userAudit.getTimestamp();
        userAudit.setTimeTaken(
                new StringBuilder()
                        .append(timeTaken)
                        .append("ms")
                        .toString()
        );
        userAudit.setMethod(method);
        userAudit.setRemoteAddress(requestWrapper.getRemoteAddr());
        userAudit.setUri(requestWrapper.getRequestURI());
        userAudit.setHost(requestWrapper.getHeader("host"));
        userAudit.setUserAgent(requestWrapper.getHeader("user-agent"));
        if (requestWrapper.getContentType() != null) {
            userAudit.setReqContentType(requestWrapper.getContentType().split("/")[1].toUpperCase());
        }
        if (responseWrapper.getContentType() != null) {
            userAudit.setRespContentType(responseWrapper.getContentType().split("/")[1].toUpperCase());
        }
        userAudit.setStatus(responseWrapper.getStatus());

        userAuditService.saveAuditLog(userAudit);
    }

    protected void updateAuditIdSeq(long newAuditId) {
//        allSequencesService.updateSeqNumber(2, newAuditId);
    }
}
