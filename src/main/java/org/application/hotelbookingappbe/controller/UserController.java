package org.application.hotelbookingappbe.controller;

import lombok.RequiredArgsConstructor;
import org.application.hotelbookingappbe.model.User;
import org.application.hotelbookingappbe.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{email}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return new ResponseEntity<>(userService.getUserByEmail(email), HttpStatus.OK);
    }

    @DeleteMapping("/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #email == principal.username)")
    public ResponseEntity<String> deleteUserByEmail(@PathVariable String email) {
        userService.deleteUserByEmail(email);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }
}
