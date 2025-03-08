package com.arjun.models;

import com.arjun.enums.ROLE;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    private ObjectId id;

    private String firstName;

    private String lastName;

    private String password;

    private String email;

    private ROLE role;

    @DBRef
    private Address address;
}
