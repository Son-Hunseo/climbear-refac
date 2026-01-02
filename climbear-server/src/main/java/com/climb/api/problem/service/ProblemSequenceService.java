package com.climb.api.problem.service;

import com.climb.api.problem.domain.entity.ProblemSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProblemSequenceService {

    private final MongoOperations mongoOperations;

    // mongoDB에 없는 auto_increment 구현
    public int getNextSequence(String sequenceName) {
        Query query = new Query(Criteria.where("_id").is(sequenceName));
        Update update = new Update().inc("seq", 1);

        ProblemSequence counter = mongoOperations.findAndModify(
                query,
                update,
                org.springframework.data.mongodb.core.FindAndModifyOptions.options().returnNew(true).upsert(true),
                ProblemSequence.class
        );

        return counter.getSeq();
    }
}