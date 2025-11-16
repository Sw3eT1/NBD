package myLibrary.repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public abstract class MongoRepository<T> {

    protected final MongoCollection<T> collection;
    protected final MongoDatabase db;

    protected MongoRepository(MongoDatabase db, String collectionName, Class<T> clazz) {
        this.db = db;
        this.collection = db.getCollection(collectionName, clazz);
    }

    public void insert(T entity) {
        collection.insertOne(entity);
    }

    public T findById(String id) {
        return collection.find(eq("_id", id)).first();
    }

    public List<T> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    public void delete(String id) {
        collection.deleteOne(eq("_id", id));
    }

    public abstract void update(T entity);
}