package org.application.hotelbookingappbe.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.application.hotelbookingappbe.model.BlacklistedToken;
import org.application.hotelbookingappbe.repository.BlacklistedTokenRepository;
import org.application.hotelbookingappbe.security.user.HotelUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private final BlacklistedTokenRepository blackListedTokenRepository;

    @Value("${jwt.secret}")
    private String secret;                  // Need for JWT token generation

    @Value("${jwt.expirationTimeMs}")
    private int expirationTimeMs;           // Token expiration time in milliseconds (1 day)

    public String generateJwtToken(Authentication authentication) {
        HotelUserDetails userPrincipal = (HotelUserDetails) authentication.getPrincipal();      // Getting user details
        List<String> roles = userPrincipal.getAuthorities()                                     // Getting user roles
                .stream()
                .map(GrantedAuthority::getAuthority).toList();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())                                 // Adding username to the "subject" part of the JWT
                .claim("id", userPrincipal.getId())                                      // Adding user ID to the JWT
                .claim("roles", roles)                                                   // Adding user roles to the JWT
                .setIssuedAt(new Date(System.currentTimeMillis()))                       // Setting the token's creation time
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMs))  // Setting the token's expiration time
                .signWith(signingKey(), SignatureAlgorithm.HS256)                        // Signing key and algorithm
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Boolean validateJwtToken(String token) {
        try {
            if (blackListedTokenRepository.existsByToken(token)) {
                return false;
            }
            Jwts.parserBuilder()
                    .setSigningKey(signingKey())
                    .build()
                    .parse(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("JWT token is invalid: {}", e.getMessage());
        }
        return false;
    }

    // Logout
    public void invalidateToken(String token) {
        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        blackListedTokenRepository.save(blacklistedToken);
    }

    private SecretKey signingKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);   // Decode the secret key from Base64
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
