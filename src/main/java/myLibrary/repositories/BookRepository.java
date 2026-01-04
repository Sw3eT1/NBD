package myLibrary.repositories;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import myLibrary.enums.BookGenre;
import myLibrary.models.Book;

public class BookRepository {

    private final CqlSession session;

    private final PreparedStatement insertStmt;
    private final PreparedStatement selectByIdStmt;
    private final PreparedStatement deleteStmt;

    public BookRepository(CqlSession session) {
        this.session = session;
        this.insertStmt = session.prepare(
                "INSERT INTO library.books_by_id (" +
                        "book_id, title, author, publisher, genre, isbn, " +
                        "publication_year, pages, language, description" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );

        this.selectByIdStmt = session.prepare(
                "SELECT book_id, title, author, publisher, genre, isbn, " +
                        "publication_year, pages, language, description " +
                        "FROM library.books_by_id WHERE book_id = ?"
        );

        this.deleteStmt = session.prepare(
                "DELETE FROM library.books_by_id WHERE book_id = ?"
        );
    }

    // CREATE
    public void insert(Book book) {
        session.execute(insertStmt.bind(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getGenre() != null ? book.getGenre().name() : null,
                book.getIsbn(),
                book.getPublicationYear(),
                book.getPages(),
                book.getLanguage(),
                book.getDescription()
        ));
    }

    // READ
    public Book findById(String id) {
        Row row = session.execute(selectByIdStmt.bind(id)).one();
        if (row == null) {
            return null;
        }

        Book book = new Book();
        book.setId(row.getString("book_id"));
        book.setTitle(row.getString("title"));
        book.setAuthor(row.getString("author"));
        book.setPublisher(row.getString("publisher"));

        String genreStr = row.getString("genre");
        if (genreStr != null) {
            book.setGenre(BookGenre.valueOf(genreStr));
        }

        book.setIsbn(row.getString("isbn"));
        book.setPublicationYear(row.getInt("publication_year"));
        book.setPages(row.getInt("pages"));
        book.setLanguage(row.getString("language"));
        book.setDescription(row.getString("description"));

        return book;
    }

    // UPDATE
    public void update(Book book) {
        insert(book);
    }

    // DELETE
    public void delete(String id) {
        session.execute(deleteStmt.bind(id));
    }
}