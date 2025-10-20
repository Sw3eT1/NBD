package myLibrary.repositories;

import jakarta.persistence.EntityManager;
import myLibrary.Book;
import myLibrary.enums.BookGenre;

import java.util.List;
import java.util.UUID;

public class BookRepository implements Repository<Book> {

    private final EntityManager em;


    public BookRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void add(Book book) {
        em.persist(book);
    }

    @Override
    public void delete(Book book) {
        em.remove(book);
    }

    @Override
    public void update(Book book) {
        em.merge(book);
    }

    @Override
    public Book find(UUID id) {
        List<Book> books = em.createQuery("SELECT b from Book b where b.id = :id", Book.class)
                .setParameter("id", id).
                getResultList();
        return books.isEmpty() ? null : books.getFirst();
    }

    @Override
    public List<Book> findAll() {
        return em.createQuery("SELECT b FROM Book b", Book.class)
                .getResultList();
    }

    public List<Book> findByTitle(String title) {
        return em.createQuery("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(:title)", Book.class)
                .setParameter("title", "%" + title + "%")
                .getResultList();
    }

    public List<Book> findByAuthor(String author) {
        return em.createQuery("SELECT b FROM Book b WHERE LOWER(b.author) LIKE LOWER(:author)", Book.class)
                .setParameter("author", "%" + author + "%")
                .getResultList();
    }

    public List<Book> findByIsbn(String isbn) {
        return em.createQuery("SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class)
                .setParameter("isbn", isbn)
                .getResultList();
    }

    public List<Book> findByGenre(BookGenre genre) {
        return em.createQuery("SELECT b FROM Book b WHERE b.genre = :genre", Book.class)
                .setParameter("genre", genre)
                .getResultList();
    }

    public boolean existsByIsbn(String isbn) {
        Long count = em.createQuery("SELECT COUNT(b) FROM Book b WHERE b.isbn = :isbn", Long.class)
                .setParameter("isbn", isbn)
                .getSingleResult();
        return count > 0;
    }


}
