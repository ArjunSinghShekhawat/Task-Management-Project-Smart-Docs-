package com.arjun.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("emails")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Email {

    @Id
    private ObjectId id;
    private String sender;
    private String recipient;
    private String subject;
    private String body;
    private Date receivedDate;
}
