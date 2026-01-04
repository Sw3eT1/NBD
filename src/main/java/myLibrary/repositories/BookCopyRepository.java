package myLibrary.repositories;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import myLibrary.enums.BookStatus;
import myLibrary.models.BookCopy;

public class BookCopyRepository {

    private final CqlSession session;

    private final PreparedStatement insertStmt;
    private final PreparedStatement selectByIdStmt;
    private final PreparedStatement deleteStmt;

    public BookCopyRepository(CqlSession session) {
        this.session = session;

        this.insertStmt = session.prepare(
                "INSERT INTO library.book_copies_by_library (" +
                        "library_id, book_id, copy_id, status" +
                        ") VALUES (?, ?, ?, ?)"
        );

        this.selectByIdStmt = session.prepare(
                "SELECT library_id, book_id, copy_id, status " +
                        "FROM library.book_copies_by_library " +
                        "WHERE library_id = ? AND book_id = ? AND copy_id = ?"
        );

        this.deleteStmt = session.prepare(
                "DELETE FROM library.book_copies_by_library " +
                        "WHERE library_id = ? AND book_id = ? AND copy_id = ?"
        );
    }

    // CREATE
    public void insert(BookCopy copy) {
        session.execute(insertStmt.bind(
                copy.getLibraryId(),
                copy.getBookId(),
                copy.getId(),
                copy.getStatus() != null ? copy.getStatus().name() : null
        ));
    }

    // READ
    public BookCopy findById(String libraryId, String bookId, String copyId) {
        Row row = session.execute(selectByIdStmt.bind(libraryId, bookId, copyId)).one();
        if (row == null) {
            return null;
        }

        BookCopy copy = new BookCopy();
        copy.setLibraryId(row.getString("library_id"));
        copy.setBookId(row.getString("book_id"));
        copy.setId(row.getString("copy_id"));

        String statusStr = row.getString("status");
        if (statusStr != null) {
            copy.setStatus(BookStatus.valueOf(statusStr));
        }

        return copy;
    }

    // UPDATE
    public void update(BookCopy copy) {
        insert(copy);
    }

    // DELETE
    public void delete(String libraryId, String bookId, String copyId) {
        session.execute(deleteStmt.bind(libraryId, bookId, copyId));
    }
}