package myLibrary.services;

import myLibrary.enums.BookStatus;
import myLibrary.enums.RentalStatus;
import myLibrary.models.BookCopy;
import myLibrary.models.Reader;
import myLibrary.models.Rental;
import myLibrary.repositories.BookCopyDao;
import myLibrary.repositories.ReaderDao;
import myLibrary.repositories.RentalDao;

import java.time.LocalDate;

public class RentalService {

    private final RentalDao rentalDao;
    private final BookCopyDao bookCopyDao;
    private final ReaderDao readerDao;

    public RentalService(RentalDao rentalDao,
                         BookCopyDao bookCopyDao,
                         ReaderDao readerDao) {
        this.rentalDao = rentalDao;
        this.bookCopyDao = bookCopyDao;
        this.readerDao = readerDao;
    }

    /**
     * Proste wypożyczenie książki z użyciem CRUD (bez transakcji Mongo).
     */
    public Rental rent(Reader reader, BookCopy copy, LocalDate dueDate) {

        if (reader == null)
            throw new IllegalArgumentException("Reader cannot be null");
        if (copy == null)
            throw new IllegalArgumentException("Book copy cannot be null");
        if (dueDate == null)
            throw new IllegalArgumentException("Due date cannot be null");

        // 1) Utwórz wypożyczenie
        Rental rental = new Rental(reader, copy, LocalDate.now(), dueDate);
        rentalDao.create(rental);

        // 2) Zmień status egzemplarza
        copy.setStatusEnum(BookStatus.RENTED);
        bookCopyDao.update(copy);

        // 3) Zwiększ licznik aktywnych wypożyczeń czytelnika
        reader.setActiveRentals(reader.getActiveRentals() + 1);
        readerDao.update(reader);

        return rental;
    }

    /**
     * Zwrot książki – logiczna operacja złożona z CRUD-ów.
     */
    public void returnBook(String readerId,
                           String rentalId,
                           String libraryId,
                           String bookId,
                           String copyId) {

        // 1) Pobierz wypożyczenie
        Rental rental = rentalDao.findById(readerId, rentalId);
        if (rental == null)
            throw new IllegalArgumentException("Rental not found: " + rentalId);

        if (rental.getStatusEnum() != RentalStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Rental is not active. Cannot return. Status = " + rental.getStatus()
            );
        }

        // 2) Pobierz kopię
        BookCopy copy = bookCopyDao.findById(libraryId, bookId, copyId);
        if (copy == null)
            throw new IllegalStateException("BookCopy not found: " + copyId);

        // 3) Oznacz wypożyczenie jako zwrócone
        rental.setStatusEnum(RentalStatus.RETURNED);
        rental.setReturnDate(LocalDate.now());
        rentalDao.update(rental);

        // 4) Oznacz kopię jako dostępną
        copy.setStatusEnum(BookStatus.AVAILABLE);
        bookCopyDao.update(copy);

        // 5) Zmniejsz licznik aktywnych wypożyczeń
        Reader reader = readerDao.findById(libraryId, readerId);
        reader.setActiveRentals(Math.max(0, reader.getActiveRentals() - 1));
        readerDao.update(reader);
    }

    // Proste przekierowania do CRUD w repo (opcjonalnie, jeśli chcesz)
    public Rental findById(String readerId, String rentalId) {
        return rentalDao.findById(readerId, rentalId);
    }

    public void delete(Rental rental) {
        rentalDao.delete(rental);
    }
}