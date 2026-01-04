package myLibrary.services;

import myLibrary.enums.BookStatus;
import myLibrary.models.Book;
import myLibrary.models.BookCopy;
import myLibrary.models.Library;
import myLibrary.repositories.BookCopyRepository;
import myLibrary.repositories.BookRepository;
import myLibrary.repositories.LibraryRepository;

public class BookCopyService {

    private final BookRepository bookRepo;
    private final LibraryRepository libraryRepo;
    private final BookCopyRepository copyRepo;

    public BookCopyService(BookRepository bookRepo,
                           LibraryRepository libraryRepo,
                           BookCopyRepository copyRepo) {
        this.bookRepo = bookRepo;
        this.libraryRepo = libraryRepo;
        this.copyRepo = copyRepo;
    }

    /**
     * Tworzy nową kopię książki w danej bibliotece.
     */
    public BookCopy createCopy(String bookId, String libraryId) {
        Book book = bookRepo.findById(bookId);
        Library library = libraryRepo.findById(libraryId);

        if (book == null) {
            throw new IllegalArgumentException("Book not found: " + bookId);
        }
        if (library == null) {
            throw new IllegalArgumentException("Library not found: " + libraryId);
        }

        BookCopy copy = new BookCopy(book, library);
        copyRepo.insert(copy);

        return copy;
    }

    /**
     * Zmiana statusu konkretnej kopii książki.
     * (klucz główny w Cassandrze: libraryId + bookId + copyId)
     */
    public void changeStatus(String libraryId,
                             String bookId,
                             String copyId,
                             BookStatus status) {

        BookCopy copy = copyRepo.findById(libraryId, bookId, copyId);
        if (copy == null) {
            throw new IllegalArgumentException("Copy not found: " + copyId);
        }

        copy.setStatus(status);
        copyRepo.update(copy);
    }
}