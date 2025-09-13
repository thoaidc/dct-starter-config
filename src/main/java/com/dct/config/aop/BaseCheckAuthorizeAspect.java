package com.dct.config.aop;

import com.dct.config.aop.annotation.CheckAuthorize;
import com.dct.model.constants.BaseExceptionConstants;
import com.dct.model.exception.BaseAuthenticationException;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * An AOP (Aspect-Oriented Programming) class in Spring <p>
 * AOP helps to separate business logic (e.g., access control checks) from the main logic of the application<p>
 * Used to check user access (authorization) before executing a method annotated with @{@link CheckAuthorize}
 * @author thoaidc
 */
public abstract class BaseCheckAuthorizeAspect {
    private static final Logger log = LoggerFactory.getLogger(BaseCheckAuthorizeAspect.class);
    private static final String ENTITY_NAME = "com.dct.config.aop.BaseCheckAuthorizeAspect";

    /**
     * {@link Pointcut} specifies where (in which method, class, or annotation) AOP logic will be applied<p>
     * This function only serves to name and define the pointcut, it does not execute any logic<p>
     * Reusability: If you need to use the same pointcut in multiple places
     * (for example, in @{@link Around}, @{@link Before}, or @{@link After} annotations),
     * you can simply reference this function
     */
    @Pointcut("@annotation(com.dct.config.aop.annotation.CheckAuthorize)") // Full path to CustomAnnotation class
    public void checkAuthorizeByJwt() {}

    /**
     * {@link Around} is a type of advice in AOP that allows you to surround the target method <p>
     * It can control the execution flow of the method (decide whether method should be executed or not)
     *
     * @return Forward the request to the target method for processing if the user has sufficient permissions
     * @throws BaseAuthenticationException If the user does not have the required permissions
     */
    @Around("checkAuthorizeByJwt()")
    public Object aroundCheckAuthorizeByJwt(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // Retrieve the annotation to check the list of required permissions for the current method
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getStaticPart().getSignature();
        CheckAuthorize annotation = methodSignature.getMethod().getAnnotation(CheckAuthorize.class);
        String[] requiredAuthorities = annotation.authorities();

        // If the user has sufficient permissions, allow the request to proceed
        if (checkAuthorize(requiredAuthorities))
            return proceedingJoinPoint.proceed();

        try {
            // Try to log the user's access attempt if they do not have permission to access the method
            ServletRequestAttributes request = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest httpServletRequest = Objects.requireNonNull(request).getRequest();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String url = httpServletRequest.getRequestURL().toString();
            String username = Objects.nonNull(authentication.getName()) ? authentication.getName() : "Anonymous";
            log.error("User '{}' does not have any permission to access this function: {}", username, url);
        } catch (Exception ignore) {}

        // Throw an exception to allow CustomExceptionHandler handling and return a response to the client
        throw new BaseAuthenticationException(ENTITY_NAME, BaseExceptionConstants.FORBIDDEN);
    }

    protected abstract boolean checkAuthorize(String[] requiredAuthorities);
}
