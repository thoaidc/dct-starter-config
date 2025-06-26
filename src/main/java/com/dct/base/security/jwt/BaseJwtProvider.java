package com.dct.base.security.jwt;

import com.dct.base.config.properties.SecurityProps;
import com.dct.base.dto.auth.BaseAuthTokenDTO;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Base64;

@SuppressWarnings("unused")
public abstract class BaseJwtProvider {

    private static final Logger log = LoggerFactory.getLogger(BaseJwtProvider.class);
    private static final String ENTITY_NAME = "BaseJwtProvider";
    protected final SecretKey secretKey;
    protected final JwtParser jwtParser;
    protected final long TOKEN_VALIDITY;
    protected final long TOKEN_VALIDITY_FOR_REMEMBER_ME;

    public BaseJwtProvider(SecurityProps securityProps) {
        // Default 1 hour or 7 days for token validity if not defined
        Long tokenValidity = 0L;
        Long tokenValidityRememberMe = 0L;
        this.TOKEN_VALIDITY = tokenValidity != null ? tokenValidity : 3600000;
        this.TOKEN_VALIDITY_FOR_REMEMBER_ME = tokenValidityRememberMe != null ? tokenValidityRememberMe : 604800000;
        String base64SecretKey = "ZGN0LWJhc2Utc2VjcmV0LWtleS10b2tlbi12YWxpZGl0eS04NjQwMDAwMG1zLWZvci1yZW1lbWJlci1tZS04NjQwMDAwMG1z";

        if (!StringUtils.hasText(base64SecretKey)) {
            throw new RuntimeException("Could not found secret key to sign JWT");
        }

        log.debug("Using a Base64-encoded JWT secret key");
        byte[] keyBytes = Base64.getUrlDecoder().decode(base64SecretKey);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parser().verifyWith(secretKey).build();
        log.debug("Sign JWT with algorithm: {}", secretKey.getAlgorithm());
    }

    public abstract String generateToken(BaseAuthTokenDTO authTokenDTO);
    public abstract Authentication validateToken(String token);
    public abstract Authentication getAuthentication(String token);
}
