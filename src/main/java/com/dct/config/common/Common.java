package com.dct.config.common;

import com.dct.model.autoconfig.DataConverterAutoConfiguration;
import com.dct.model.common.JsonUtils;
import com.dct.model.constants.BaseExceptionConstants;
import com.dct.model.constants.BaseHttpStatusConstants;
import com.dct.model.dto.auth.BaseUserDTO;
import com.dct.model.dto.auth.JwtDTO;
import com.dct.model.dto.response.BaseResponseDTO;
import com.dct.model.exception.BaseAuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Common {
    private static final Logger log = LoggerFactory.getLogger(Common.class);
    private static final ObjectMapper objectMapper = DataConverterAutoConfiguration.buildObjectMapper();
    private static final String ENTITY_NAME = "com.dct.config.common.Common";

    public static void handleUnauthorizedError(HttpServletResponse response, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // Convert response body to JSON
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(BaseHttpStatusConstants.UNAUTHORIZED);
        BaseResponseDTO responseDTO = BaseResponseDTO.builder()
                .code(BaseHttpStatusConstants.UNAUTHORIZED)
                .success(Boolean.FALSE)
                .message(message)
                .build();
        response.getWriter().write(JsonUtils.toJsonString(responseDTO));
        response.flushBuffer();
    }

    public static BaseUserDTO getUserWithAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof BaseUserDTO user) {
                return user;
            }

            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = Objects.nonNull(attrs) ? attrs.getRequest() : null;
            String jwt = Objects.nonNull(request) ? request.getHeader(HttpHeaders.AUTHORIZATION) : null;

            if (!StringUtils.hasText(jwt)) {
                jwt = authentication.getCredentials().toString();
            }

            JwtDTO jwtDTO = Optional.ofNullable(getInfoJwt(jwt)).orElseGet(JwtDTO::new);
            BaseUserDTO.userBuilder()
                    .withId(jwtDTO.getUserId())
                    .withShopId(jwtDTO.getShopId())
                    .withUsername(jwtDTO.getUsername())
                    .withAuthorities(Arrays.stream(jwtDTO.getAuthorities().split(",")).collect(Collectors.toSet()))
                    .build();
        }

        throw new BaseAuthenticationException(ENTITY_NAME, BaseExceptionConstants.BAD_CREDENTIALS);
    }

    public static JwtDTO getInfoJwt(String jwt) {
        try {
            String[] chunks = jwt.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            return objectMapper.readValue(payload, JwtDTO.class);
        } catch (Exception e) {
            log.error("[GET_JWT_INFO_ERROR] - Could not extract JWT info: {}", e.getMessage());
        }

        return null;
    }
}
