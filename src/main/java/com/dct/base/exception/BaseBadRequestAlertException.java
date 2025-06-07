package com.dct.base.exception;

@SuppressWarnings("unused")
public class BaseBadRequestAlertException extends BaseException {

    public BaseBadRequestAlertException(String entityName, String errorKey) {
        super(entityName, errorKey, null, null);
    }

    public BaseBadRequestAlertException(String entityName, String errorKey, Object[] args, Throwable error) {
        super(entityName, errorKey, args, error);
    }
}
