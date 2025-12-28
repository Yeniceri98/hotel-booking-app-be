package org.application.hotelbookingappbe.security;

import org.application.hotelbookingappbe.repository.BlacklistedTokenRepository;
import org.application.hotelbookingappbe.security.jwt.JwtUtils;
import org.application.hotelbookingappbe.security.user.HotelUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @InjectMocks
    private JwtUtils jwtUtils;

    private Authentication authentication;

    @BeforeEach
    void init() {
        authentication = mock(Authentication.class);

        HotelUserDetails principal = new HotelUserDetails(
                5L,
                "ahmet@mail.com",
                "encoded",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(authentication.getPrincipal()).thenReturn(principal);

        // HS256 için yeterli uzunlukta bir secret üretip Base64 encode ediyoruz
        byte[] raw = "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8); // 32 bytes
        String base64Secret = Base64.getEncoder().encodeToString(raw);

        // @Value alanlarını testte manuel set ediyoruz
        ReflectionTestUtils.setField(jwtUtils, "secret", base64Secret);
        ReflectionTestUtils.setField(jwtUtils, "expirationTimeMs", 60_000); // 1 dakika
    }

    @Test
    void generateJwtToken_shouldGenerateNonNullToken() {
        String token = jwtUtils.generateJwtToken(authentication);

        assertNotNull(token);
        assertTrue(token.length() > 10);
    }

    @Test
    void getUsernameFromJwtToken_shouldReturnSubject() {
        String token = jwtUtils.generateJwtToken(authentication);

        String username = jwtUtils.getUsernameFromJwtToken(token);

        assertEquals("ahmet@mail.com", username);
    }

    @Test
    void validateJwtToken_whenNotBlacklistedAndValid_shouldReturnTrue() {
        String token = jwtUtils.generateJwtToken(authentication);

        when(blacklistedTokenRepository.existsByToken(token)).thenReturn(false);

        Boolean valid = jwtUtils.validateJwtToken(token);

        assertTrue(valid);

        verify(blacklistedTokenRepository).existsByToken(token);
    }

    @Test
    void validateJwtToken_whenBlacklisted_shouldReturnFalse() {
        String token = jwtUtils.generateJwtToken(authentication);

        when(blacklistedTokenRepository.existsByToken(token)).thenReturn(true);

        Boolean valid = jwtUtils.validateJwtToken(token);

        assertFalse(valid);

        // blacklist true ise parse etmeye bile gerek yok; metot direkt false döner
        verify(blacklistedTokenRepository).existsByToken(token);
    }

    @Test
    void validateJwtToken_whenExpired_shouldReturnFalse() {
        // expirationTimeMs negatif -> token oluşturulduğu anda "expired" gibi davranır
        ReflectionTestUtils.setField(jwtUtils, "expirationTimeMs", -1);

        String expiredToken = jwtUtils.generateJwtToken(authentication);

        when(blacklistedTokenRepository.existsByToken(expiredToken)).thenReturn(false);

        Boolean valid = jwtUtils.validateJwtToken(expiredToken);

        assertFalse(valid);
    }
}