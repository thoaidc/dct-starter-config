package com.dct.base.security.jwt;

import com.dct.base.config.properties.JwtProps;
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
import java.util.Optional;

@SuppressWarnings("unused")
public abstract class BaseJwtProvider {

    private static final Logger log = LoggerFactory.getLogger(BaseJwtProvider.class);
    private static final String ENTITY_NAME = "BaseJwtProvider";
    protected final SecretKey secretKey;
    protected final JwtParser jwtParser;
    protected final long ACCESS_TOKEN_VALIDITY;
    protected final long REFRESH_TOKEN_VALIDITY;
    protected final long REFRESH_TOKEN_VALIDITY_FOR_REMEMBER;

    public BaseJwtProvider(JwtProps jwtProps) {
        JwtProps jwtConfig = Optional.ofNullable(jwtProps).orElse(new JwtProps());
        String base64SecretKey = jwtConfig.getBase64SecretKey();
        ACCESS_TOKEN_VALIDITY = jwtConfig.getAccessToken().getValidity();
        REFRESH_TOKEN_VALIDITY = jwtConfig.getRefreshToken().getValidity();
        REFRESH_TOKEN_VALIDITY_FOR_REMEMBER = jwtConfig.getRefreshToken().getValidityForRemember();

        if (!StringUtils.hasText(base64SecretKey)) {
            throw new RuntimeException("Could not found secret key to sign JWT");
        }

        log.debug("Using a Base64-encoded JWT secret key");
        byte[] keyBytes = Base64.getUrlDecoder().decode(base64SecretKey);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parser().verifyWith(secretKey).build();
        log.debug("Sign JWT with algorithm: {}", secretKey.getAlgorithm());
    }

    public abstract String generateAccessToken(BaseAuthTokenDTO authTokenDTO);
    public abstract String generateRefreshToken(BaseAuthTokenDTO authTokenDTO);
    public abstract Authentication validateToken(String token);
    public abstract Authentication getAuthentication(String token);
}
