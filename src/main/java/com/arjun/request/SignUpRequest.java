package com.arjun.request;

import com.arjun.enums.ROLE;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 1, message = "First name cannot be empty")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 1, message = "Last name cannot be empty")
    private String lastName;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$",
            message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, and one digit.")
    private String password;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Role cannot be null")
    private ROLE role;
}
