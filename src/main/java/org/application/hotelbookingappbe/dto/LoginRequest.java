package org.application.hotelbookingappbe.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank
    @Size(min = 3, max = 255, message = "Password must be between 3 and 255 characters long")
    private String password;
}
