package org.application.hotelbookingappbe.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.application.hotelbookingappbe.security.jwt.JwtAuthFilter;
import org.application.hotelbookingappbe.security.jwt.JwtUtils;
import org.application.hotelbookingappbe.security.user.HotelUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HotelUserDetailsService hotelUserDetailsService;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void init() {
        jwtAuthFilter = new JwtAuthFilter();

        // Filter içinde field injection var (@Autowired). Unit testte bunu manuel set etmeliyiz.
        ReflectionTestUtils.setField(jwtAuthFilter, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(jwtAuthFilter, "userDetailsService", hotelUserDetailsService);

        // Her testten önce SecurityContext temizliği önemli (aksi halde testler birbirini etkiler)
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_whenTokenValid_shouldSetSecurityContext_andContinueChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtUtils.validateJwtToken("valid-token")).thenReturn(true);
        when(jwtUtils.getUsernameFromJwtToken("valid-token")).thenReturn("ahmet@mail.com");

        // Spring Security UserDetails implementasyonu (basit)
        UserDetails userDetails = new User("ahmet@mail.com", "encoded", Collections.emptyList());

        when(hotelUserDetailsService.loadUserByUsername("ahmet@mail.com")).thenReturn(userDetails);

        jwtAuthFilter.doFilter(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("ahmet@mail.com", SecurityContextHolder.getContext().getAuthentication().getName());

        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_whenNoAuthorizationHeader_shouldJustContinueChain_andNotAuthenticate() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        jwtAuthFilter.doFilter(request, response, chain);

        // Authorization header yok -> parseJwt null -> authentication set edilmemeli
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtUtils);
        verifyNoInteractions(hotelUserDetailsService);
    }

    @Test
    void doFilterInternal_whenTokenInvalid_shouldContinueChain_withoutAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtUtils.validateJwtToken("invalid")).thenReturn(false);

        jwtAuthFilter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtils).validateJwtToken("invalid");
        verify(chain).doFilter(request, response);
        verifyNoInteractions(hotelUserDetailsService); // token invalid -> username fetch edilmeyecek
    }

    @Test
    void doFilterInternal_whenUserNotFound_shouldReturn401_andStopChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtUtils.validateJwtToken("valid-token")).thenReturn(true);
        when(jwtUtils.getUsernameFromJwtToken("valid-token")).thenReturn("missing@mail.com");

        when(hotelUserDetailsService.loadUserByUsername("missing@mail.com"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        jwtAuthFilter.doFilter(request, response, chain);

        // filter içinde UsernameNotFoundException yakalanınca 401 set edip return ediyor.
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertTrue(response.getContentAsString().contains("User not found"));

        // burada chain.doFilter çağrılmamalı (return ile çıkılıyor)
        verify(chain, never()).doFilter(any(), any());
    }
}

