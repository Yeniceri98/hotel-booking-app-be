package org.application.hotelbookingappbe.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.application.hotelbookingappbe.model.User;
import org.application.hotelbookingappbe.dto.LoginRequest;
import org.application.hotelbookingappbe.dto.LoginResponse;
import org.application.hotelbookingappbe.security.jwt.JwtUtils;
import org.application.hotelbookingappbe.security.user.HotelUserDetails;
import org.application.hotelbookingappbe.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Auth Controller", description = "Authentication API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Tag(name = "Register User")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody User user) {
        userService.register(user);
        return new ResponseEntity<>("Registration successful", HttpStatus.CREATED);
    }

    @Tag(name = "Login User")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), loginRequest.getPassword()
                )
        );

        // SecurityContextHolder, SecurityContext’e her yerden erişim sağlayan holderdır
        SecurityContextHolder.getContext().setAuthentication(authentication);   // Authentication, SecurityContext'e set edildi
        String jwtToken = jwtUtils.generateJwtToken(authentication);            // Generating JWT token

        HotelUserDetails userDetails = (HotelUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Returning the JWT token and user details from the Login response
        return ResponseEntity.ok(new LoginResponse(userDetails.getId(), userDetails.getEmail(), jwtToken, roles));
    }

    @Tag(name = "Logout User")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            jwtUtils.invalidateToken(token);      // Invalidate the token
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok("Logged out successfully");
        }
        return ResponseEntity.badRequest().body("Invalid token");
    }
}
