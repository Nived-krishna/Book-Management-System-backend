package com.example.bookmanager.repository;

import com.example.bookmanager.model.Counter;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CounterRepository extends MongoRepository<Counter, String> {
    // No methods needed for basic upsert via MongoOperations
}
