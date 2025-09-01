package com.dct.config.security.filter;

import com.dct.model.config.properties.SecurityProps;
import com.dct.model.dto.auth.BaseTokenDTO;
import com.dct.model.security.AbstractJwtProvider;

@SuppressWarnings("unused")
public abstract class BaseJwtProvider extends AbstractJwtProvider {
    public BaseJwtProvider(SecurityProps securityProps) {
        super(securityProps);
    }

    public abstract String generateAccessToken(BaseTokenDTO tokenDTO);
    public abstract String generateRefreshToken(BaseTokenDTO tokenDTO);
}
