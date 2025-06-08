package com.dct.base.aop;

import com.dct.base.aop.annotation.CheckAuthorize;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default check authorize aspect extends by {@link BaseCheckAuthorizeAspect} <p>
 * Used to check user access (authorization) before executing a method annotated with @{@link CheckAuthorize} <p>
 *
 * `@{@link Aspect}` mark this class as an Aspect in AOP
 *
 * @author thoaidc
 */
@Aspect
public class DefaultCheckAuthorizeAspect extends BaseCheckAuthorizeAspect {

    @Override
    protected boolean checkAuthorize(String[] requiredAuthorities) {
        // Check against the list of permissions of the current user in security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> userAuthorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return userAuthorities.containsAll(Arrays.asList(requiredAuthorities));
    }
}
