package myLibrary.repositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import myLibrary.models.Reader;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.lookup;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.eq;

public class ReaderRepository extends MongoRepository<Reader> {

    public ReaderRepository(MongoClient client, MongoDatabase db) {
        super(client, db, "readers", Reader.class);
    }

    @Override
    public void update(Reader reader) {
        collection.replaceOne(eq("_id", reader.getId()), reader);
    }

    public Reader findByCardNumber(String cardNumber) {
        return collection.find(eq("cardNumber", cardNumber)).first();
    }

    public List<Reader> findBySurname(String surname) {
        return collection.find(eq("surname", surname)).into(new ArrayList<>());
    }

    public MongoCollection<Reader> getCollection() {
        return db.getCollection("readers", Reader.class);
    }


    public Document getReaderWithType(String readerId) {

        List<Bson> pipeline = List.of(
                match(eq("_id", readerId)),
                lookup("readerTypes", "readerTypeId", "_id", "type")
        );

        return db.getCollection("readers", Document.class)
                .aggregate(pipeline)
                .first();
    }
}
