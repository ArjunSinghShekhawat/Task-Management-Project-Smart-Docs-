package com.arjun.dto;

import com.arjun.enums.ROLE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private ObjectId id;
    private String firstName;
    private String lastName;
    private String email;
    private ROLE role;
    private Date registrationDate;
    private AddressDto address;
    private String imageUrl;
}
