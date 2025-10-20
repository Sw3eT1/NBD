package myLibrary.managers;

import jakarta.persistence.EntityManager;
import myLibrary.Book;
import myLibrary.BookCopy;
import myLibrary.ReaderType;
import myLibrary.repositories.BookRepository;

import java.util.UUID;

public class BookManager extends BaseManager {
    private final BookRepository bookRepo;

    public BookManager(EntityManager em) {
        super(em);
        this.bookRepo = new BookRepository(em);
    }

    public void addBook(Book book) {
        executeInTransaction(() -> bookRepo.add(book));
        System.out.println("Dodano książkę: " + book.getTitle());
    }

    public void removeBook(Book book) {
        executeInTransaction(() -> bookRepo.delete(book));
    }

    public void updateBook(Book book) {
        executeInTransaction(() -> bookRepo.update(book));
    }

    public Book findBook(UUID id) {
        return bookRepo.find(id);
    }

    public void addAllowedReaderType(Book book, ReaderType type) {
        executeInTransaction(() -> {
            book.addAllowedType(type);
            bookRepo.update(book);
        });
    }

    public void addBookCopy(Book book, BookCopy copy) {
        executeInTransaction(() -> {
            book.addCopy(copy);
            bookRepo.update(book);
        });
    }
}

