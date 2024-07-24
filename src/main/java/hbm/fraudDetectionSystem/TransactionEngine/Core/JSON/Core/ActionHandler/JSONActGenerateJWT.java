package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.ActionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.User;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.UserServiceImpl;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.domain.UserPrincipal;
import hbm.fraudDetectionSystem.SecurityEngine.Utility.JWTTokenProvider;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils.JSONFieldHeaderDictionary;
import hbm.fraudDetectionSystem.TransactionEngine.Exception.TransactionEngineException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component("jsonActGenerateJWT")
@Slf4j
public class JSONActGenerateJWT implements JSONActionProcessor {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;
    private final UserServiceImpl userService;

    public JSONActGenerateJWT(AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider, UserServiceImpl userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }


    @Override
    public JsonNode run(JsonNode on, ObjectNode pn, String value, JSONFieldHeaderDictionary fConfig, String args) throws JsonProcessingException, TransactionEngineException {
        try {
            String formatter = fConfig.getFormatter().getFormat();
            String[] splitValue = formatter.split("\\+");

            String username = "";
            String password = "";

            for (int i = 0; i < splitValue.length; i++) {
                switch (i) {
                    case 0:
                        username = pn.findPath(splitValue[i].trim()).asText();
                        break;
                    case 1:
                        password = pn.findPath(splitValue[i].trim()).asText();
                        break;
                }
            }

            LOGGER.info(
                    String.format(
                            "Generate JWT auth \n\tUsername: [%s]",
                            username
                    )
            );
            User loginUser = userService.findByUsername(username);

            try {
                this.authenticate(username, password);
            } catch (AuthenticationException e) {
                this.userService.validateLoginAttempt(loginUser);
                throw e;
            }
            UserPrincipal userPrincipal = new UserPrincipal(loginUser);

            String token = this.getJwtHeader(userPrincipal);

            LOGGER.info(
                    String.format(
                            "Generated JWT auth: [%s]",
                            token
                    )
            );

            return TextNode.valueOf(token);
        } catch (Exception e) {
            throw new TransactionEngineException("Unexpected error generate JWT", e);
        }
    }

    private String getJwtHeader(UserPrincipal userPrincipal) {
        return jwtTokenProvider.generateJwtToken(userPrincipal);
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
