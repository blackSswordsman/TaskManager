package com.example.taskmanager.utils;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

@UtilityClass
public class UserUtils {
    public static Optional<String> getCurrentUserEmail() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt principal) {
            return Optional.ofNullable(principal.getClaims().get("email"))
                    .filter(String.class::isInstance)
                    .map(String.class::cast);
        } else {
            return Optional.empty();
        }

    }

}
