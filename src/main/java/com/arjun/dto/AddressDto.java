package com.arjun.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {

    private String country;
    private String street;
    private String city;
    private String state;
    private String pinCode;
}
