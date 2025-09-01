package com.dct.config.exception;

import com.dct.model.constants.BaseExceptionConstants;
import com.dct.model.constants.BaseHttpStatusConstants;
import com.dct.model.dto.response.BaseResponseDTO;
import com.dct.model.exception.BaseAuthenticationException;
import com.dct.model.exception.BaseBadRequestAlertException;
import com.dct.model.exception.BaseBadRequestException;
import com.dct.model.exception.BaseException;
import com.dct.model.exception.BaseIllegalArgumentException;
import com.dct.model.exception.BaseInternalServerException;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

/**
 * Used to handle exceptions in the application centrally and return consistent responses <p>
 * Provides a standardized and centralized approach to handling common errors in Spring applications <p>
 * Helps log detailed errors, return structured responses, and easily internationalize error messages
 *
 * @author thoaidc
 */
@ControllerAdvice
public abstract class BaseExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(BaseExceptionHandler.class);

    /**
     * Handle exceptions when an HTTP method is not supported (ex: calling POST on an endpoint that only supports GET)
     * @param e the exception to handle
     * @param headers the headers to use for the response
     * @param status the status code to use for the response
     * @param request the current request
     * @return ResponseEntity with body is a BaseResponseDTO with custom message I18n
     */
    @Override
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(@Nullable HttpRequestMethodNotSupportedException e,
                                                                      @Nullable HttpHeaders headers,
                                                                      @Nullable HttpStatusCode status,
                                                                      @Nullable WebRequest request) {
        log.error("[METHOD_NOT_ALLOWED_EXCEPTION] - message: {}", Objects.nonNull(e) ? e.getMessage() : "");
        BaseResponseDTO responseDTO = convertResponse(
            BaseHttpStatusConstants.METHOD_NOT_ALLOWED,
            BaseExceptionConstants.METHOD_NOT_ALLOW
        );
        return new ResponseEntity<>(responseDTO, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Handle exceptions when a request is invalid due to data (validation error)<p>
     * For example: When using @{@link Valid} in a controller method and the incoming request data is invalid
     *
     * @param exception the exception to handle
     * @param headers the headers to be written to the response
     * @param status the selected response status
     * @param request the current request
     * @return ResponseEntity with body is a BaseResponseDTO with custom message I18n
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  @Nullable HttpHeaders headers,
                                                                  @Nullable HttpStatusCode status,
                                                                  @Nullable WebRequest request) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        String errorKey = BaseExceptionConstants.INVALID_REQUEST_DATA; // Default message

        if (Objects.nonNull(fieldError)) {
            errorKey = fieldError.getDefaultMessage(); // If the field with an error includes a custom message key
        }

        log.error("[INVALID_REQUEST_DATA_EXCEPTION] - message: {}", exception.getMessage());
        BaseResponseDTO responseDTO = convertResponse(BaseHttpStatusConstants.BAD_REQUEST, errorKey);
        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e,
                                                                       @Nullable HttpHeaders headers,
                                                                       @Nullable HttpStatusCode status,
                                                                       WebRequest request) {
        log.error("[MAXIMUM_UPLOAD_EXCEPTION] at: {} - {}", request.getClass().getName(), e.getMessage());
        BaseResponseDTO responseDTO = convertResponse(
            BaseHttpStatusConstants.BAD_REQUEST,
            BaseExceptionConstants.MAXIMUM_UPLOAD_SIZE_EXCEEDED
        );
        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@Nullable HttpMessageNotReadableException ex,
                                                                  @Nullable HttpHeaders headers,
                                                                  @Nullable HttpStatusCode status,
                                                                  @Nullable WebRequest request) {
        log.error("[HTTP_MESSAGE_NOT_READABLE] - error: ", ex);
        BaseResponseDTO responseDTO = convertResponse(
            BaseHttpStatusConstants.UNPROCESSABLE_ENTITY,
            BaseExceptionConstants.INVALID_REQUEST_DATA
        );
        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(@Nullable HttpMessageNotWritableException ex,
                                                                  @Nullable HttpHeaders headers,
                                                                  @Nullable HttpStatusCode status,
                                                                  @Nullable WebRequest request) {
        log.error("[HTTP_MESSAGE_NOT_WRITABLE] - error: ", ex);
        BaseResponseDTO responseDTO = convertResponse(
            BaseHttpStatusConstants.UNPROCESSABLE_ENTITY,
            BaseExceptionConstants.UNCERTAIN_ERROR
        );
        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ BaseAuthenticationException.class })
    public ResponseEntity<BaseResponseDTO> handleBaseAuthenticationException(BaseAuthenticationException exception) {
        log.error("[AUTHENTICATION_EXCEPTION] - at: {} ", exception.getEntityName(), exception.getError());
        String errorMessage = StringUtils.hasText(exception.getOriginalMessage())
                ? exception.getOriginalMessage()
                : exception.getErrorKey();
        BaseResponseDTO responseDTO = convertResponse(BaseHttpStatusConstants.UNAUTHORIZED, errorMessage);
        return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ BaseBadRequestException.class, BaseBadRequestAlertException.class })
    public ResponseEntity<BaseResponseDTO> handleBaseBadRequestException(BaseException exception) {
        log.error("[BAD_REQUEST_EXCEPTION] - at: {}", exception.getEntityName(), exception.getError());
        String errorMessage = StringUtils.hasText(exception.getOriginalMessage())
                ? exception.getOriginalMessage()
                : exception.getErrorKey();
        BaseResponseDTO responseDTO = convertResponse(BaseHttpStatusConstants.BAD_REQUEST, errorMessage);
        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ BaseIllegalArgumentException.class })
    public ResponseEntity<BaseResponseDTO> handleBaseIllegalArgumentException(BaseIllegalArgumentException exception) {
        log.error("[ILLEGAL_ARGUMENT_EXCEPTION] - at: {}", exception.getEntityName(), exception.getError());
        String errorMessage = StringUtils.hasText(exception.getOriginalMessage())
                ? exception.getOriginalMessage()
                : exception.getErrorKey();
        BaseResponseDTO responseDTO = convertResponse(BaseHttpStatusConstants.UNPROCESSABLE_ENTITY, errorMessage);
        return new ResponseEntity<>(responseDTO, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({ BaseInternalServerException.class })
    public ResponseEntity<BaseResponseDTO> handleBaseInternalServerException(BaseInternalServerException exception) {
        log.error("[INTERNAL_SERVER_EXCEPTION] - at: {}", exception.getEntityName(), exception.getError());
        String errorMessage = StringUtils.hasText(exception.getOriginalMessage())
                ? exception.getOriginalMessage()
                : exception.getErrorKey();
        BaseResponseDTO responseDTO = convertResponse(BaseHttpStatusConstants.INTERNAL_SERVER_ERROR, errorMessage);
        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ BaseException.class })
    public ResponseEntity<BaseResponseDTO> handleBaseException(BaseException exception) {
        log.error("[BASE_EXCEPTION] - at: {}", exception.getEntityName(), exception.getError());
        String errorMessage = StringUtils.hasText(exception.getOriginalMessage())
                ? exception.getOriginalMessage()
                : exception.getErrorKey();
        BaseResponseDTO responseDTO = convertResponse(BaseHttpStatusConstants.INTERNAL_SERVER_ERROR, errorMessage);
        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ NullPointerException.class })
    public ResponseEntity<Object> handleNullPointerException(NullPointerException exception, WebRequest request) {
        // Handle NullPointerException (include of Objects.requireNonNull())
        log.error("[NULL_POINTER_EXCEPTION] - at: {}, message: {}", request.getClass().getName(), exception.getMessage());
        BaseResponseDTO responseDTO = convertResponse(
            BaseHttpStatusConstants.INTERNAL_SERVER_ERROR,
            BaseExceptionConstants.NULL_EXCEPTION
        );
        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ RuntimeException.class })
    public ResponseEntity<BaseResponseDTO> handleRuntimeException(RuntimeException exception) {
        log.error("[RUNTIME_EXCEPTION] - error: ", exception);
        BaseResponseDTO responseDTO = convertResponse(
            BaseHttpStatusConstants.INTERNAL_SERVER_ERROR,
            BaseExceptionConstants.UNCERTAIN_ERROR
        );
        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<BaseResponseDTO> handleException(Exception exception) {
        log.error("[GENERAL_EXCEPTION] - error: ", exception);
        BaseResponseDTO responseDTO = convertResponse(
            BaseHttpStatusConstants.INTERNAL_SERVER_ERROR,
            BaseExceptionConstants.UNCERTAIN_ERROR
        );
        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private BaseResponseDTO convertResponse(int code, String message) {
        log.error("Error: {}", message);
        return BaseResponseDTO.builder().code(code).success(Boolean.FALSE).message(message).build();
    }
}
