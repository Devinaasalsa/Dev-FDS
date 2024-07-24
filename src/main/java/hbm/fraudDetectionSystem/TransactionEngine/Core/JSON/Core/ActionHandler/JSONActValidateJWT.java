package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.ActionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import hbm.fraudDetectionSystem.SecurityEngine.Constant.SecurityConstant;
import hbm.fraudDetectionSystem.SecurityEngine.Utility.JWTTokenProvider;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils.JSONFieldHeaderDictionary;
import hbm.fraudDetectionSystem.TransactionEngine.Exception.TransactionEngineException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("jsonActValidateJWT")
@Slf4j
public class JSONActValidateJWT implements JSONActionProcessor {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final JWTTokenProvider jwtTokenProvider;

    @Autowired
    public JSONActValidateJWT(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public JsonNode run(JsonNode on, ObjectNode pn, String value, JSONFieldHeaderDictionary fConfig, String args) throws JsonProcessingException, TransactionEngineException {
        LOGGER.info(
                String.format(
                        "Start validate token value: %s",
                        value
                )
        );

        String clrToken = value.replace(SecurityConstant.TOKEN_HEADER, "");
        LOGGER.info(
                String.format(
                        "Clear token: %s",
                        clrToken
                )
        );

        String username = this.getSubject(clrToken);

        LOGGER.info(
                String.format(
                        "Username from token: %s",
                        username
                )
        );
        LOGGER.info("Start validating token");

        if (!this.jwtTokenProvider.isTokenValid(username, clrToken)) {
            throw new TransactionEngineException("Token isn't valid");
        }

        LOGGER.info("Status: [VALID]");

        return TextNode.valueOf(clrToken);
    }

    protected String getSubject(String clrToken) throws TransactionEngineException {
        try {
            return jwtTokenProvider.getSubject(clrToken);
        } catch (Exception e) {
            throw new TransactionEngineException("Token isn't valid", e);
        }
    }
}
