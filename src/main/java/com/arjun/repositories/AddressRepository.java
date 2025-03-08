package com.arjun.repositories;

import com.arjun.models.Address;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends MongoRepository<Address, ObjectId> {
}
