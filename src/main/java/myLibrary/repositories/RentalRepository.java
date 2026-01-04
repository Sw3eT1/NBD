package myLibrary.repositories;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import myLibrary.enums.RentalStatus;
import myLibrary.models.Rental;

public class RentalRepository {

    private final CqlSession session;

    private final PreparedStatement insertStmt;
    private final PreparedStatement selectByIdStmt;
    private final PreparedStatement deleteStmt;

    public RentalRepository(CqlSession session) {
        this.session = session;

        this.insertStmt = session.prepare(
                "INSERT INTO library.rentals_by_reader (" +
                        "reader_id, rental_id, book_copy_id, rental_date, " +
                        "due_date, return_date, status, fine" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        );

        this.selectByIdStmt = session.prepare(
                "SELECT reader_id, rental_id, book_copy_id, rental_date, " +
                        "due_date, return_date, status, fine " +
                        "FROM library.rentals_by_reader " +
                        "WHERE reader_id = ? AND rental_id = ?"
        );

        this.deleteStmt = session.prepare(
                "DELETE FROM library.rentals_by_reader " +
                        "WHERE reader_id = ? AND rental_id = ?"
        );
    }

    // CREATE
    public void insert(Rental rental) {
        session.execute(insertStmt.bind(
                rental.getReaderId(),
                rental.getId(),
                rental.getBookCopyId(),
                rental.getRentalDate(),
                rental.getDueDate(),
                rental.getReturnDate(),
                rental.getStatus() != null ? rental.getStatus().name() : null,
                rental.getFine()
        ));
    }

    // READ
    public Rental findById(String readerId, String rentalId) {
        Row row = session.execute(selectByIdStmt.bind(readerId, rentalId)).one();
        if (row == null) {
            return null;
        }

        Rental r = new Rental();
        r.setReaderId(row.getString("reader_id"));
        r.setId(row.getString("rental_id"));
        r.setBookCopyId(row.getString("book_copy_id"));
        r.setRentalDate(row.getLocalDate("rental_date"));
        r.setDueDate(row.getLocalDate("due_date"));
        r.setReturnDate(row.getLocalDate("return_date"));

        String statusStr = row.getString("status");
        if (statusStr != null) {
            r.setStatus(RentalStatus.valueOf(statusStr));
        }

        r.setFine(row.getDouble("fine"));

        return r;
    }

    // UPDATE = upsert
    public void update(Rental rental) {
        insert(rental);
    }

    // DELETE
    public void delete(String readerId, String rentalId) {
        session.execute(deleteStmt.bind(readerId, rentalId));
    }
}