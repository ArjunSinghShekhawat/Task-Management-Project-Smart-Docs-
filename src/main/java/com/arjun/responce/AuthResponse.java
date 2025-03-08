package com.arjun.responce;

import com.arjun.enums.ROLE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String message;
    private boolean status;
    private String jwt;
    private ROLE role;
}
