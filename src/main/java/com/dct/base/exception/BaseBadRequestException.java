package com.dct.base.exception;

@SuppressWarnings("unused")
public class BaseBadRequestException extends BaseException {

    public BaseBadRequestException(String entityName, String errorKey) {
        super(entityName, errorKey, null, null);
    }

    public BaseBadRequestException(String entityName, String errorKey, Object[] args, Throwable error) {
        super(entityName, errorKey, args, error);
    }
}
