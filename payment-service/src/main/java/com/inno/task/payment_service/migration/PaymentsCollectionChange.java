package com.inno.task.payment_service.migration;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationLevel;
import com.mongodb.client.model.ValidationOptions;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@ChangeUnit(id = "001-create-payments", order = "001", author = "mikhail")
public class PaymentsCollectionChange {

    private static final String COLLECTION_NAME = "payments";

    private final MongoTemplate mongoTemplate;

    public PaymentsCollectionChange(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Execution
    public void execution() {
        MongoDatabase db = mongoTemplate.getDb();

        ValidationOptions validator = new ValidationOptions()
                .validator(new Document("$jsonSchema", new Document()
                        .append("bsonType", "object")
                        .append("required", List.of(
                                "order_id", "user_id", "status", "timestamp", "payment_amount"))
                        .append("properties", new Document()
                                .append("order_id", new Document("bsonType", "long"))
                                .append("user_id", new Document("bsonType", "long"))
                                .append("status", new Document("bsonType", "string")
                                        .append("enum", List.of("SUCCESS", "FAILED")))
                                .append("timestamp", new Document("bsonType", "date"))
                                .append("payment_amount", new Document("bsonType", "decimal")))))
                .validationLevel(ValidationLevel.STRICT)
                .validationAction(ValidationAction.ERROR);

        db.createCollection(COLLECTION_NAME,
                new CreateCollectionOptions().validationOptions(validator));

        db.getCollection(COLLECTION_NAME).createIndex(
                new Document("order_id", 1),
                new IndexOptions().name("idx_payments_order_id").unique(true));

        db.getCollection(COLLECTION_NAME).createIndex(
                new Document("user_id", 1).append("timestamp", -1),
                new IndexOptions().name("idx_payments_user_ts"));
    }

    @RollbackExecution
    public void rollback() {
        mongoTemplate.getDb().getCollection(COLLECTION_NAME).drop();
    }
}