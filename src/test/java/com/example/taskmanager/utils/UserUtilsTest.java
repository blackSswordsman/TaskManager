package com.example.taskmanager.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUtilsTest {

    @Test
    void givenJwtWithEmail_whenGetCurrentUserEmail_thenReturnsEmail() {
        // Создаем мок JWT с email
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaims()).thenReturn(Map.of("email", "test@example.com"));

        // Создаем мок Authentication, возвращающий JWT
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Создаем мок SecurityContext, возвращающий Authentication
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Мокаем SecurityContextHolder
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Вызываем метод и проверяем результат
            Optional<String> email = UserUtils.getCurrentUserEmail();
            assertTrue(email.isPresent());
            assertEquals("test@example.com", email.get());
        }
    }

    @Test
    void givenNoJwtPrincipal_whenGetCurrentUserEmail_thenReturnsEmpty() {
        // Создаем мок Authentication, возвращающий не Jwt principal
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("Not a JWT");

        // Создаем мок SecurityContext, возвращающий Authentication
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Мокаем SecurityContextHolder
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Вызываем метод и проверяем результат
            Optional<String> email = UserUtils.getCurrentUserEmail();
            assertTrue(email.isEmpty());
        }
    }
}
