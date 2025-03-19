package com.arjun.repositories;

import com.arjun.models.Email;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface EmailRepository extends MongoRepository<Email, ObjectId> {
    boolean existsBySenderAndSubjectAndReceivedDate(String sender, String subject, Date receivedDate);
}
