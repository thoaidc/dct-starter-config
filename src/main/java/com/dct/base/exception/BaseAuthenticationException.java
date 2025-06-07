package com.dct.base.exception;

@SuppressWarnings("unused")
public class BaseAuthenticationException extends BaseException {

    public BaseAuthenticationException(String entityName, String errorKey) {
        super(entityName, errorKey, null, null);
    }

    public BaseAuthenticationException(String entityName, String errorKey, Object[] args, Throwable error) {
        super(entityName, errorKey, args, error);
    }
}
