package myLibrary.services;

import myLibrary.enums.BookStatus;
import myLibrary.enums.RentalStatus;
import myLibrary.models.BookCopy;
import myLibrary.models.Reader;
import myLibrary.models.Rental;
import myLibrary.repositories.BookCopyRepository;
import myLibrary.repositories.ReaderRepository;
import myLibrary.repositories.RentalRepository;

import java.time.LocalDate;

public class RentalService {

    private final RentalRepository rentalRepo;
    private final BookCopyRepository copyRepo;
    private final ReaderRepository readerRepo;

    public RentalService(RentalRepository rentalRepo,
                         BookCopyRepository copyRepo,
                         ReaderRepository readerRepo) {
        this.rentalRepo = rentalRepo;
        this.copyRepo = copyRepo;
        this.readerRepo = readerRepo;
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
        rentalRepo.insert(rental);

        // 2) Zmień status egzemplarza
        copy.setStatus(BookStatus.RENTED);
        copyRepo.update(copy);

        // 3) Zwiększ licznik aktywnych wypożyczeń czytelnika
        reader.setActiveRentals(reader.getActiveRentals() + 1);
        readerRepo.update(reader);

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
        Rental rental = rentalRepo.findById(readerId, rentalId);
        if (rental == null)
            throw new IllegalArgumentException("Rental not found: " + rentalId);

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Rental is not active. Cannot return. Status = " + rental.getStatus()
            );
        }

        // 2) Pobierz kopię
        BookCopy copy = copyRepo.findById(libraryId, bookId, copyId);
        if (copy == null)
            throw new IllegalStateException("BookCopy not found: " + copyId);

        // 3) Oznacz wypożyczenie jako zwrócone
        rental.setStatus(RentalStatus.RETURNED);
        rental.setReturnDate(LocalDate.now());
        rentalRepo.update(rental);

        // 4) Oznacz kopię jako dostępną
        copy.setStatus(BookStatus.AVAILABLE);
        copyRepo.update(copy);

        // 5) Zmniejsz licznik aktywnych wypożyczeń
        Reader reader = readerRepo.findById(libraryId, readerId);
        reader.setActiveRentals(Math.max(0, reader.getActiveRentals() - 1));
        readerRepo.update(reader);
    }

    // Proste przekierowania do CRUD w repo (opcjonalnie, jeśli chcesz)
    public Rental findById(String readerId, String rentalId) {
        return rentalRepo.findById(readerId, rentalId);
    }

    public void delete(String readerId, String rentalId) {
        rentalRepo.delete(readerId, rentalId);
    }
}