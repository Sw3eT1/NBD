package myLibrary.managers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import myLibrary.Book;
import myLibrary.BookCopy;
import myLibrary.ReaderType;
import myLibrary.repositories.BookRepository;

import java.util.UUID;

public class BookManager {

    private final EntityManager em;
    private final BookRepository bookRepo;

    public BookManager(EntityManager em) {
        this.em = em;
        this.bookRepo = new BookRepository(em);
    }

    public void addBook(Book book) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            bookRepo.add(book);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
        System.out.println("Dodano książkę: " + book.getTitle());
    }

    public void removeBook(Book book) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            bookRepo.delete(book);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void updateBook(Book book) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            bookRepo.update(book);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public Book findBook(UUID id) {
        return bookRepo.find(id);
    }

    public void addAllowedReaderType(Book book, ReaderType type) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            book.addAllowedType(type);
            bookRepo.update(book);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void addBookCopy(Book book, BookCopy copy) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            book.addCopy(copy);
            bookRepo.update(book);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}
