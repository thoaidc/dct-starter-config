package com.dct.config.security.filter;

import com.dct.model.config.properties.SecurityProps;
import com.dct.model.dto.auth.BaseTokenDTO;
import com.dct.model.exception.BaseException;
import com.dct.model.exception.BaseIllegalArgumentException;
import com.dct.model.security.AbstractJwtProvider;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Base64;

@SuppressWarnings("unused")
public abstract class BaseJwtProvider extends AbstractJwtProvider {
    private static final Logger log = LoggerFactory.getLogger(BaseJwtProvider.class);
    private static final String ENTITY_NAME = "com.dct.model.security.filter.BaseJwtProvider";
    protected final JwtParser refreshTokenParser;
    protected final SecretKey refreshTokenSecretKey;
    protected final long REFRESH_TOKEN_VALIDITY;
    protected final long REFRESH_TOKEN_VALIDITY_FOR_REMEMBER;

    public BaseJwtProvider(SecurityProps securityProps) {
        super(securityProps);
        SecurityProps.JwtConfig jwtConfig = securityProps.getJwt();
        String base64RefreshTokenSecretKey = jwtConfig.getRefreshToken().getBase64SecretKey();
        REFRESH_TOKEN_VALIDITY = jwtConfig.getRefreshToken().getValidity();
        REFRESH_TOKEN_VALIDITY_FOR_REMEMBER = jwtConfig.getRefreshToken().getValidityForRemember();

        if (!StringUtils.hasText(base64RefreshTokenSecretKey)) {
            throw new BaseIllegalArgumentException(ENTITY_NAME, "Could not found secret key to sign refresh JWT");
        }

        byte[] refreshTokenKeyBytes = Base64.getUrlDecoder().decode(base64RefreshTokenSecretKey);
        refreshTokenSecretKey = Keys.hmacShaKeyFor(refreshTokenKeyBytes);
        refreshTokenParser = Jwts.parser().verifyWith(refreshTokenSecretKey).build();
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            super.parseToken(refreshTokenParser, refreshToken);
            return true;
        } catch (BaseException e) {
            log.error("[VALIDATE_REFRESH_TOKEN_ERROR] - error: {}", e.getErrorKey());
        }

        return false;
    }

    public abstract String generateAccessToken(BaseTokenDTO tokenDTO);
    public abstract String generateRefreshToken(BaseTokenDTO tokenDTO);
}
