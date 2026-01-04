package myLibrary.services;

import myLibrary.models.Book;
import myLibrary.repositories.BookRepository;

public class BookService {

    private final BookRepository bookRepo;

    public BookService(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    public void addBook(Book book) {
        bookRepo.insert(book);
    }

    public Book find(String id) {
        return bookRepo.findById(id);
    }

    public void updateBook(Book book) {
        bookRepo.update(book);
    }

    public void deleteBook(String id) {
        bookRepo.delete(id);
    }
}