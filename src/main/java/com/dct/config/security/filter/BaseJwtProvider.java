package com.dct.config.security.filter;

import com.dct.model.config.properties.SecurityProps;
import com.dct.model.constants.BaseSecurityConstants;
import com.dct.model.dto.auth.BaseTokenDTO;
import com.dct.model.dto.auth.BaseUserDTO;
import com.dct.model.exception.BaseIllegalArgumentException;
import com.dct.model.security.AbstractJwtProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public abstract class BaseJwtProvider extends AbstractJwtProvider {
    private static final Logger log = LoggerFactory.getLogger(BaseJwtProvider.class);
    private static final String ENTITY_NAME = "com.dct.model.security.filter.BaseJwtProvider";
    protected final JwtParser refreshTokenParser;
    protected final SecretKey refreshTokenSecretKey;
    protected final long ACCESS_TOKEN_VALIDITY;
    protected final long REFRESH_TOKEN_VALIDITY;
    protected final long REFRESH_TOKEN_VALIDITY_FOR_REMEMBER;

    public BaseJwtProvider(SecurityProps securityProps) {
        super(securityProps);
        SecurityProps.JwtConfig jwtConfig = securityProps.getJwt();
        String base64RefreshTokenSecretKey = jwtConfig.getRefreshToken().getBase64SecretKey();
        ACCESS_TOKEN_VALIDITY = jwtConfig.getAccessToken().getValidity();
        REFRESH_TOKEN_VALIDITY = jwtConfig.getRefreshToken().getValidity();
        REFRESH_TOKEN_VALIDITY_FOR_REMEMBER = jwtConfig.getRefreshToken().getValidityForRemember();

        if (!StringUtils.hasText(base64RefreshTokenSecretKey)) {
            throw new BaseIllegalArgumentException(ENTITY_NAME, "Could not found secret key to sign refresh JWT");
        }

        byte[] refreshTokenKeyBytes = Base64.getUrlDecoder().decode(base64RefreshTokenSecretKey);
        refreshTokenSecretKey = Keys.hmacShaKeyFor(refreshTokenKeyBytes);
        refreshTokenParser = Jwts.parser().verifyWith(refreshTokenSecretKey).build();
    }

    public Authentication validateRefreshToken(String refreshToken) {
        Claims claims = super.parseToken(this.refreshTokenParser, refreshToken);
        return getAuthentication(claims);
    }

    @Override
    protected Authentication getAuthentication(Claims claims) {
        try {
            Integer userId = (Integer) claims.get(BaseSecurityConstants.TOKEN_PAYLOAD.USER_ID);
            Integer shopId = null;

            try {
                shopId = (Integer) claims.get(BaseSecurityConstants.TOKEN_PAYLOAD.SHOP_ID);
            } catch (Exception ignored){}

            String username = (String) claims.get(BaseSecurityConstants.TOKEN_PAYLOAD.USERNAME);
            String authorities = (String) claims.get(BaseSecurityConstants.TOKEN_PAYLOAD.AUTHORITIES);
            Set<SimpleGrantedAuthority> userAuthorities = Arrays.stream(authorities.split(","))
                    .filter(StringUtils::hasText)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
            BaseUserDTO principal = BaseUserDTO.userBuilder()
                    .withId(userId)
                    .withShopId(shopId)
                    .withUsername(username)
                    .withAuthorities(userAuthorities)
                    .build();
            return new UsernamePasswordAuthenticationToken(principal, username, userAuthorities);
        } catch (Exception e) {
            log.error("[JWT_PROVIDER_GET_AUTHENTICATION_ERROR] - error: {}", e.getMessage());
            throw new BaseIllegalArgumentException(ENTITY_NAME, "Could not get authentication from token");
        }
    }

    public abstract String generateAccessToken(BaseTokenDTO tokenDTO);
    public abstract String generateRefreshToken(BaseTokenDTO tokenDTO);
}
