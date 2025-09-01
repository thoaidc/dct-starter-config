package com.dct.config.security.filter;

import com.dct.model.config.properties.SecurityProps;
import com.dct.model.constants.BaseSecurityConstants;
import com.dct.model.dto.auth.BaseTokenDTO;
import com.dct.model.dto.auth.BaseUserDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class DefaultJwtProvider extends BaseJwtProvider {
    private static final Logger log = LoggerFactory.getLogger(DefaultJwtProvider.class);
    private static final String ENTITY_NAME = "com.dct.config.security.filter.DefaultJwtProvider";

    public DefaultJwtProvider(SecurityProps securityProps) {
        super(securityProps);
    }

    @Override
    protected Authentication getAuthentication(Claims claims) {
        log.debug("[RETRIEVE_AUTHENTICATION] - Claim authentication info from token after authenticated");
        Integer userId = (Integer) claims.get(BaseSecurityConstants.TOKEN_PAYLOAD.USER_ID);
        String username = (String) claims.get(BaseSecurityConstants.TOKEN_PAYLOAD.USERNAME);
        String authorities = (String) claims.get(BaseSecurityConstants.TOKEN_PAYLOAD.AUTHORITIES);

        Set<SimpleGrantedAuthority> userAuthorities = Arrays.stream(authorities.split(","))
                .filter(StringUtils::hasText)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        BaseUserDTO principal = BaseUserDTO.userBuilder()
                .withId(userId)
                .withUsername(username)
                .withPassword(username) // Not used but need to avoid `argument 'content': null` error in spring security
                .withAuthorities(userAuthorities)
                .build();

        return new UsernamePasswordAuthenticationToken(principal, username, userAuthorities);
    }

    public String generateAccessToken(BaseTokenDTO tokenDTO) {
        long tokenValidityInMilliseconds = Instant.now().toEpochMilli() + ACCESS_TOKEN_VALIDITY;
        return generateToken(tokenDTO, tokenValidityInMilliseconds);
    }

    public String generateRefreshToken(BaseTokenDTO tokenDTO) {
        long tokenValidityInMilliseconds = Instant.now().toEpochMilli();

        if (tokenDTO.isRememberMe())
            tokenValidityInMilliseconds += this.REFRESH_TOKEN_VALIDITY_FOR_REMEMBER;
        else
            tokenValidityInMilliseconds += this.REFRESH_TOKEN_VALIDITY;

        return generateToken(tokenDTO, tokenValidityInMilliseconds);
    }

    private String generateToken(BaseTokenDTO tokenDTO, long tokenValidity) {
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
}
