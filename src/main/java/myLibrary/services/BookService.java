package myLibrary.services;

import myLibrary.models.Book;
import myLibrary.repositories.BookDao;

public class BookService {

    private final BookDao bookDao;

    public BookService(BookDao bookRepo) {
        this.bookDao = bookRepo;
    }

    public void addBook(Book book) {
        bookDao.create(book);
    }

    public Book find(String id) {
        return bookDao.findById(id);
    }

    public void updateBook(Book book) {
        bookDao.update(book);
    }

    public void deleteBook(Book book) {
        bookDao.delete(book);
    }
}