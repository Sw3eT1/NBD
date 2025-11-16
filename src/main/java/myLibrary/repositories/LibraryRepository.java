package myLibrary.repositories;

import com.mongodb.client.MongoDatabase;
import myLibrary.models.Library;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.lookup;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.eq;

public class LibraryRepository extends MongoRepository<Library> {

    public LibraryRepository(MongoDatabase db) {
        super(db, "libraries", Library.class);
    }

    @Override
    public void update(Library library) {
        collection.replaceOne(eq("_id", library.getId()), library);
    }

    public List<Library> findByCity(String city) {
        return collection.find(eq("address.city", city)).into(new ArrayList<>());
    }

    public Document getLibraryWithCopies(String libraryId) {

        List<Bson> pipeline = List.of(
                match(eq("_id", libraryId)),
                lookup("bookCopies", "_id", "libraryId", "copies")
        );

        return db.getCollection("libraries", Document.class)
                .aggregate(pipeline)
                .first();
    }
}


