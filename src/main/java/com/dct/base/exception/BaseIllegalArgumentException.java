package com.dct.base.exception;

@SuppressWarnings("unused")
public class BaseIllegalArgumentException extends BaseException {

    public BaseIllegalArgumentException(String entityName, String errorKey) {
        super(entityName, errorKey, null, null);
    }

    public BaseIllegalArgumentException(String entityName, String errorKey, Object[] args, Throwable error) {
        super(entityName, errorKey, args, error);
    }
}
