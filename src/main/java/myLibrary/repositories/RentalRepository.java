package myLibrary.repositories;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.model.*;

import myLibrary.enums.BookStatus;
import myLibrary.enums.RentalStatus;
import myLibrary.models.BookCopy;
import myLibrary.models.Reader;
import myLibrary.models.Rental;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class RentalRepository extends MongoRepository<Rental> {

    public RentalRepository(MongoClient client, MongoDatabase db) {
        super(client, db, "rentals", Rental.class);
    }


    @Override
    public void update(Rental rental) {
        var result = collection.replaceOne(eq("_id", rental.getId()), rental);
        if (result.getMatchedCount() == 0) {
            throw new IllegalStateException("Rental not found for id: " + rental.getId());
        }
    }

    public boolean hasActiveRental(String readerId, String copyId) {
        return collection.countDocuments(and(
                eq("readerId", readerId),
                eq("bookCopyId", copyId),
                eq("status", RentalStatus.ACTIVE.toString())
        )) > 0;
    }

    public List<Rental> findActiveRentals() {
        return collection.find(eq("status", RentalStatus.ACTIVE.toString()))
                .into(new ArrayList<>());
    }

    public List<Rental> findActiveByReader(String readerId) {
        return collection.find(and(
                eq("readerId", readerId),
                eq("status", RentalStatus.ACTIVE.toString())
        )).into(new ArrayList<>());
    }

    // findById w rodzicu już istnieje – nie nadpisujemy

    public Document getRentalDetails(String rentalId) {

        List<Bson> pipeline = List.of(
                match(eq("_id", rentalId)),
                lookup("bookCopies", "bookCopyId", "_id", "copy"),
                unwind("$copy"),
                lookup("readers", "readerId", "_id", "reader"),
                unwind("$reader"),
                lookup("books", "copy.bookId", "_id", "book"),
                unwind("$book")
        );

        return db.getCollection("rentals", Document.class)
                .aggregate(pipeline)
                .first();
    }

    /**
     * Atomiczne wypożyczenie książki:
     * - sprawdza limit czytelnika
     * - sprawdza duplikaty
     * - aktualizuje status egzemplarza
     * - wstawia rekord wypożyczenia
     */
    public boolean tryRent(Reader reader, BookCopy copy, LocalDate dueDate) {

        TransactionOptions txnOptions = TransactionOptions.builder()
                .readConcern(ReadConcern.SNAPSHOT)
                .writeConcern(WriteConcern.MAJORITY)
                .readPreference(ReadPreference.primary())
                .build();

        try (ClientSession session = client.startSession()) {
            return session.withTransaction(() -> {

                String readerId = reader.getId();
                String copyId = copy.getId();

                MongoCollection<Document> rentals = db.getCollection("rentals");
                MongoCollection<Document> copies = db.getCollection("bookCopies");
                MongoCollection<Document> readers = db.getCollection("readers");
                MongoCollection<Document> types = db.getCollection("readerTypes");

                // 🔥 Pobierz typ czytelnika
                Document rt = types.find(session, eq("_id", reader.getReaderTypeId())).first();
                if (rt == null)
                    throw new IllegalStateException("ReaderType not found");

                int maxBooks = rt.getInteger("maxBooks");

                // 🔥 ATOMICZNE ZWIĘKSZENIE LICZNIKA
                // Czytelnik dostanie wypożyczenie tylko jeśli activeRentals < maxBooks
                Document updatedReader = readers.findOneAndUpdate(
                        session,
                        and(
                                eq("_id", readerId),
                                lt("activeRentals", maxBooks)
                        ),
                        combine(
                                inc("activeRentals", 1)  // atomiczny increment
                        ),
                        new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
                );

                // jeśli null → limit został osiągnięty
                if (updatedReader == null)
                    return false;

                // 🔥 atomiczne zablokowanie książki
                Document updatedCopy = copies.findOneAndUpdate(
                        session,
                        and(
                                eq("_id", copyId),
                                eq("status", BookStatus.AVAILABLE.toString())
                        ),
                        set("status", BookStatus.RENTED.toString()),
                        new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
                );

                if (updatedCopy == null) {
                    // wycofaj increment
                    readers.updateOne(session, eq("_id", readerId), inc("activeRentals", -1));
                    return false;
                }

                // 🔥 wstawienie wypożyczenia
                Rental rental = new Rental(reader, copy, LocalDate.now(), dueDate);

                rentals.insertOne(session, new Document()
                        .append("_id", rental.getId())
                        .append("readerId", readerId)
                        .append("bookCopyId", copyId)
                        .append("status", rental.getStatus().toString())
                        .append("fine", rental.getFine())
                        .append("rentalDate", rental.getRentalDate())
                        .append("dueDate", rental.getDueDate())
                );

                return true;

            }, txnOptions);
        }
    }

}
