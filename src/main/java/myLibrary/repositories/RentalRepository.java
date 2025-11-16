package myLibrary.repositories;

import com.mongodb.client.MongoDatabase;
import myLibrary.models.Rental;
import myLibrary.enums.RentalStatus;
import org.bson.Document;
import org.bson.conversions.Bson;


import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.lookup;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.*;

public class RentalRepository extends MongoRepository<Rental> {

    public RentalRepository(MongoDatabase db) {
        super(db, "rentals", Rental.class);
    }

    @Override
    public void update(Rental rental) {
        collection.replaceOne(eq("_id", rental.getId()), rental);
    }

    public boolean hasActiveRental(String readerId, String copyId) {
        return collection.countDocuments(and(
                eq("readerId", readerId),
                eq("bookCopyId", copyId),
                eq("status", RentalStatus.ACTIVE)
        )) > 0;
    }

    public List<Rental> findActiveRentals() {
        return collection.find(eq("status", RentalStatus.ACTIVE)).into(new ArrayList<>());
    }

    public Rental findById(String id) {
        return collection.find(eq("_id", id)).first();
    }

    public Document getRentalDetails(String rentalId) {

        List<Bson> pipeline = List.of(
                match(eq("_id", rentalId)),
                lookup("bookCopies", "bookCopyId", "_id", "copy"),
                lookup("readers", "readerId", "_id", "reader"),
                lookup("books", "copy.bookId", "_id", "book")
        );

        return db.getCollection("rentals", Document.class)
                .aggregate(pipeline)
                .first();
    }
}