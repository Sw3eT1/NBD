package myLibrary.repositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import myLibrary.models.BookCopy;
import myLibrary.enums.BookStatus;
import org.bson.Document;
import org.bson.conversions.Bson;


import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.lookup;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.*;

public class BookCopyRepository extends MongoRepository<BookCopy> {

    public BookCopyRepository(MongoClient client, MongoDatabase db) {
        super(client, db, "bookCopies", BookCopy.class);
    }

    @Override
    public void update(BookCopy copy) {
        collection.replaceOne(eq("_id", copy.getId()), copy);
    }

    public List<BookCopy> findByBookId(String bookId) {
        return collection.find(eq("bookId", bookId)).into(new ArrayList<>());
    }

    public List<BookCopy> findAvailableCopies(String bookId) {
        return collection.find(and(
                eq("bookId", bookId),
                eq("status", BookStatus.AVAILABLE)
        )).into(new ArrayList<>());
    }

    public Document getCopyWithBookAndLibrary(String copyId) {

        List<Bson> pipeline = List.of(
                match(eq("_id", copyId)),
                lookup("books", "bookId", "_id", "book"),
                lookup("libraries", "libraryId", "_id", "library")
        );

        return db.getCollection("bookCopies", Document.class)
                .aggregate(pipeline)
                .first();
    }
}