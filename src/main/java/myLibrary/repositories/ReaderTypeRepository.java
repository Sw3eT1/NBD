package myLibrary.repositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import myLibrary.models.ReaderType;

import static com.mongodb.client.model.Filters.eq;

public class ReaderTypeRepository extends MongoRepository<ReaderType> {

    public ReaderTypeRepository(MongoClient client, MongoDatabase db) {
        super(client, db, "readerTypes", ReaderType.class);
    }

    @Override
    public void update(ReaderType type) {
        collection.replaceOne(eq("_id", type.getId()), type);
    }

    public ReaderType findByName(String name) {
        return collection.find(eq("name", name)).first();
    }
}
