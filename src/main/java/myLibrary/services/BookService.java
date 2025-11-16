package myLibrary.services;

import myLibrary.models.Book;
import myLibrary.models.BookCopy;
import myLibrary.repositories.BookRepository;
import myLibrary.repositories.BookCopyRepository;

import java.util.List;

public class BookService {

    private final BookRepository bookRepo;
    private final BookCopyRepository copyRepo;

    public BookService(BookRepository bookRepo, BookCopyRepository copyRepo) {
        this.bookRepo = bookRepo;
        this.copyRepo = copyRepo;
    }

    public void addBook(Book book) {
        bookRepo.insert(book);
    }

    public Book find(String id) {
        return bookRepo.findById(id);
    }

    public List<BookCopy> getCopies(String bookId) {
        return copyRepo.findByBookId(bookId);
    }

    public void updateBook(Book book) {
        bookRepo.update(book);
    }

}

