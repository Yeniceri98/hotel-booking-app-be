package org.application.hotelbookingappbe.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class LoginResponse {
    private Long id;
    private String email;
    private String token;   // Token value comes from here
    private String type = "Bearer";
    private List<String> roles;

    public LoginResponse(Long id, String email, String token, List<String> roles) {
        this.id = id;
        this.email = email;
        this.token = token;
        this.roles = roles;
    }
}
