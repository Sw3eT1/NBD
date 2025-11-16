package myLibrary.services;

import myLibrary.models.Book;
import myLibrary.models.BookCopy;
import myLibrary.models.Library;
import myLibrary.enums.BookStatus;
import myLibrary.repositories.BookCopyRepository;
import myLibrary.repositories.BookRepository;
import myLibrary.repositories.LibraryRepository;


public class BookCopyService {

    private final BookRepository bookRepo;
    private final LibraryRepository libraryRepo;
    private final BookCopyRepository copyRepo;

    public BookCopyService(BookRepository bookRepo, LibraryRepository libraryRepo,
                           BookCopyRepository copyRepo) {
        this.bookRepo = bookRepo;
        this.libraryRepo = libraryRepo;
        this.copyRepo = copyRepo;
    }

    public BookCopy createCopy(String bookId, String libraryId) {
        Book book = bookRepo.findById(bookId);
        Library library = libraryRepo.findById(libraryId);

        if (book == null || library == null)
            throw new IllegalArgumentException("Book or Library not found.");

        BookCopy copy = new BookCopy(book, library);
        copyRepo.insert(copy);

        return copy;
    }

    public void changeStatus(BookCopy copy, BookStatus status) {
        copy.setStatus(status);
        copyRepo.update(copy);
    }
}

