package com.arjun.dto;

import com.arjun.enums.ROLE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String firstName;
    private String lastName;
    private String email;
    private ROLE role;
    private AddressDto address;
}
