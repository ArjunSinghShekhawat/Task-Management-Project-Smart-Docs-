package com.arjun.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotNull(message = "Email cannot be null")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Password cannot be null")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$",
            message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, and one digit.")
    private String password;

}
