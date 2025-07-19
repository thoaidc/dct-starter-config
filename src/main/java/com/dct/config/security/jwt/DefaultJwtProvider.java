package com.dct.config.security.jwt;

import com.dct.model.config.properties.JwtProps;
import com.dct.model.constants.BaseExceptionConstants;
import com.dct.model.constants.BaseSecurityConstants;
import com.dct.model.dto.auth.BaseAuthTokenDTO;
import com.dct.model.exception.BaseAuthenticationException;
import com.dct.model.exception.BaseBadRequestException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.security.SignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultJwtProvider extends BaseJwtProvider {

    private static final Logger log = LoggerFactory.getLogger(DefaultJwtProvider.class);
    private static final String ENTITY_NAME = "DefaultJwtProvider";

    public DefaultJwtProvider(JwtProps jwtProps) {
        super(jwtProps);
    }

    private String generateToken(BaseAuthTokenDTO tokenDTO, long tokenValidity) {
        Authentication authentication = tokenDTO.getAuthentication();
        Set<String> userAuthorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        long validityInMilliseconds = Instant.now().toEpochMilli() + tokenValidity;

        return Jwts.builder()
                .subject(authentication.getName())
                .claim(BaseSecurityConstants.TOKEN_PAYLOAD.USER_ID, tokenDTO.getUserId())
                .claim(BaseSecurityConstants.TOKEN_PAYLOAD.USERNAME, tokenDTO.getUsername())
                .claim(BaseSecurityConstants.TOKEN_PAYLOAD.AUTHORITIES, String.join(",", userAuthorities))
                .signWith(secretKey)
                .issuedAt(new Date())
                .expiration(new Date(validityInMilliseconds))
                .compact();
    }

    @Override
    public String generateAccessToken(BaseAuthTokenDTO authTokenDTO) {
        log.debug("[{}] - Generate access token by default config", ENTITY_NAME);
        long tokenValidityInMilliseconds = Instant.now().toEpochMilli() + ACCESS_TOKEN_VALIDITY;
        return generateToken(authTokenDTO, tokenValidityInMilliseconds);
    }

    @Override
    public String generateRefreshToken(BaseAuthTokenDTO authTokenDTO) {
        log.debug("[{}] - Generate refresh token by default config", ENTITY_NAME);
        long tokenValidityInMilliseconds = Instant.now().toEpochMilli();

        if (authTokenDTO.isRememberMe())
            tokenValidityInMilliseconds += this.REFRESH_TOKEN_VALIDITY_FOR_REMEMBER;
        else
            tokenValidityInMilliseconds += this.REFRESH_TOKEN_VALIDITY;

        return generateToken(authTokenDTO, tokenValidityInMilliseconds);
    }

    @Override
    public Authentication validateToken(String token) {
        log.debug("[{}] - Validate token by default config", ENTITY_NAME);

        if (!StringUtils.hasText(token))
            throw new BaseBadRequestException(ENTITY_NAME, BaseExceptionConstants.BAD_CREDENTIALS);

        try {
            return getAuthentication(token);
        } catch (MalformedJwtException e) {
            log.error("[{}] - Invalid JWT: {}", ENTITY_NAME, e.getMessage());
        } catch (SignatureException e) {
            log.error("[{}] - Invalid JWT signature: {}", ENTITY_NAME, e.getMessage());
        } catch (SecurityException e) {
            log.error("[{}] - Unable to decode JWT: {}", ENTITY_NAME, e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("[{}] - Expired JWT: {}", ENTITY_NAME, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("[{}] - Invalid JWT string (null, empty,...): {}", ENTITY_NAME, e.getMessage());
        }

        throw new BaseAuthenticationException(ENTITY_NAME, BaseExceptionConstants.TOKEN_INVALID_OR_EXPIRED);
    }

    @Override
    public Authentication getAuthentication(String token) {
        Claims claims = (Claims) jwtParser.parse(token).getPayload();
        String authorities = (String) claims.get(BaseSecurityConstants.TOKEN_PAYLOAD.AUTHORITIES);

        if (!StringUtils.hasText(authorities)) {
            throw new BaseAuthenticationException(ENTITY_NAME, BaseExceptionConstants.FORBIDDEN);
        }

        Collection<SimpleGrantedAuthority> userAuthorities = Arrays.stream(authorities.split(","))
                .filter(StringUtils::hasText)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        User principal = new User(claims.getSubject(), "none-password", userAuthorities);
        return new UsernamePasswordAuthenticationToken(principal, token, userAuthorities);
    }
}
