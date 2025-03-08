package com.arjun.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Address {

    @Id
    private ObjectId id;

    private String country;

    private String street;

    private String city;

    private String state;

    private String pinCode;
}
