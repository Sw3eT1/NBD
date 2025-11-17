package myLibrary.repositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import myLibrary.models.Book;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.lookup;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.*;

public class BookRepository extends MongoRepository<Book> {

    public BookRepository(MongoClient client, MongoDatabase db) {
        super(client, db, "books", Book.class);
    }

    @Override
    public void update(Book book) {
        collection.replaceOne(eq("_id", book.getId()), book);
    }

    public List<Book> findByTitle(String title) {
        return collection.find(regex("title", title, "i")).into(new ArrayList<>());
    }

    public boolean existsByIsbn(String isbn) {
        return collection.find(eq("isbn", isbn)).limit(1).first() != null;
    }

    public Document getBookWithCopies(String bookId) {

        List<Bson> pipeline = List.of(
                match(eq("_id", bookId)),
                lookup("bookCopies", "_id", "bookId", "copies")
        );

        return db.getCollection("books", Document.class)
                .aggregate(pipeline)
                .first();
    }
}