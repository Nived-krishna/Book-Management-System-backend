package com.example.bookmanager.service;

import com.example.bookmanager.model.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
public class SequenceGeneratorService {
    private final MongoOperations mongoOperations;

    @Autowired
    public SequenceGeneratorService(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    /**
     * Generate a unique book UID in the form B-001, B-002, ...
     * Uses an upserted counter document to increment atomically.
     */
    public String generateBookUid() {
        final String seqName = "book_sequence";

        Query query = new Query(Criteria.where("_id").is(seqName));
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);

        Counter counter = mongoOperations.findAndModify(query, update, options, Counter.class);

        long seqVal = (counter != null) ? counter.getSeq() : 1L;
        // Format to B-001 (3 digits) - adjust %03d to widen digits
        return String.format("B-%03d", seqVal);
    }
}
