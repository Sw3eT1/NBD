package myLibrary.services;

import myLibrary.enums.BookStatus;
import myLibrary.models.Book;
import myLibrary.models.BookCopy;
import myLibrary.models.Library;
import myLibrary.repositories.BookCopyDao;
import myLibrary.repositories.BookDao;
import myLibrary.repositories.LibraryDao;

public class BookCopyService {

    private final BookDao bookDao;
    private final LibraryDao libraryDao;
    private final BookCopyDao bookCopyDao;

    public BookCopyService(BookDao bookRepo,
                           LibraryDao libraryRepo,
                           BookCopyDao copyRepo) {
        this.bookDao = bookRepo;
        this.libraryDao = libraryRepo;
        this.bookCopyDao = copyRepo;
    }

    /**
     * Tworzy nową kopię książki w danej bibliotece.
     */
    public BookCopy createCopy(String bookId, String libraryId) {
        Book book = bookDao.findById(bookId);
        Library library = libraryDao.findById(libraryId);

        if (book == null) {
            throw new IllegalArgumentException("Book not found: " + bookId);
        }
        if (library == null) {
            throw new IllegalArgumentException("Library not found: " + libraryId);
        }

        BookCopy copy = new BookCopy(book, library);
        bookCopyDao.create(copy);

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

        BookCopy copy = bookCopyDao.findById(libraryId, bookId, copyId);
        if (copy == null) {
            throw new IllegalArgumentException("Copy not found: " + copyId);
        }

        copy.setStatusEnum(status);
        bookCopyDao.update(copy);
    }
}