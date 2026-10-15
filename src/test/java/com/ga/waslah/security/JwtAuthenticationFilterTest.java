package com.ga.waslah.security;

import com.ga.waslah.model.Role;
import com.ga.waslah.model.User;
import com.ga.waslah.model.UserStatus;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private MyUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void shouldAuthenticateUserWithValidJwtToken()
            throws Exception {

        // Arrange
        String token = "valid-jwt-token";
        String username = "noor";

        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);

        UserDetails userDetails =
                new MyUserDetails(user);

        when(jwtService.extractUsername(token))
                .thenReturn(username);

        when(userDetailsService.loadUserByUsername(username))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(token, userDetails))
                .thenReturn(true);

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        // Act
        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        // Assert
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNotNull(authentication);

        assertEquals(
                username,
                authentication.getName()
        );

        assertTrue(
                authentication
                        .getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_USER")
                        )
        );

        verify(jwtService).extractUsername(token);

        verify(jwtService)
                .isTokenValid(token, userDetails);

        verify(filterChain)
                .doFilter(request, response);
    }
    @Test
    void shouldNotAuthenticateUserWithInvalidJwtToken()
            throws Exception {

        // Arrange
        String token = "invalid-jwt-token";

        when(jwtService.extractUsername(token))
                .thenReturn("noor");

        User user = new User();
        user.setUsername("noor");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);

        UserDetails userDetails =
                new MyUserDetails(user);

        when(userDetailsService.loadUserByUsername("noor"))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(token, userDetails))
                .thenReturn(false);

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        // Act
        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        // Assert
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNull(authentication);

        verify(jwtService).extractUsername(token);

        verify(jwtService)
                .isTokenValid(token, userDetails);

        verify(filterChain)
                .doFilter(request, response);
    }
}