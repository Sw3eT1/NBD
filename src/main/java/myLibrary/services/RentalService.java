package myLibrary.services;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import myLibrary.enums.BookStatus;
import myLibrary.enums.RentalStatus;
import myLibrary.models.BookCopy;
import myLibrary.models.Library;
import myLibrary.models.Reader;
import myLibrary.models.Rental;
import myLibrary.kafka.RentalEventProducer;
import myLibrary.repositories.LibraryRepository;
import myLibrary.repositories.BookCopyRepository;
import myLibrary.repositories.ReaderRepository;
import myLibrary.repositories.RentalRepository;
import org.bson.Document;

import java.time.LocalDate;
import java.util.List;

public class RentalService {

    private final RentalRepository rentalRepo;
    private final BookCopyRepository copyRepo;
    private final ReaderRepository readerRepo;
    private final LibraryRepository libraryRepo;
    private final RentalEventProducer eventProducer;

    public RentalService(RentalRepository rentalRepo, BookCopyRepository copyRepo, ReaderRepository readerRepo) {
        this.rentalRepo = rentalRepo;
        this.copyRepo = copyRepo;
        this.readerRepo = readerRepo;
        this.libraryRepo = null;
        this.eventProducer = null;
    }

    /**
     * Konstruktor rozszerzony o integrację z Apache Kafka.
     *
     * @param libraryRepo repozytorium bibliotek (potrzebne do pobrania nazwy wypożyczalni)
     * @param eventProducer producent zdarzeń (Kafka)
     */
    public RentalService(RentalRepository rentalRepo,
                         BookCopyRepository copyRepo,
                         ReaderRepository readerRepo,
                         LibraryRepository libraryRepo,
                         RentalEventProducer eventProducer) {
        this.rentalRepo = rentalRepo;
        this.copyRepo = copyRepo;
        this.readerRepo = readerRepo;
        this.libraryRepo = libraryRepo;
        this.eventProducer = eventProducer;
    }

    /**
     * Wypożyczenie książki (transakcja w repozytorium)
     */
    public void rent(Reader reader, BookCopy copy, LocalDate dueDate) {

        if (reader == null)
            throw new IllegalArgumentException("Reader cannot be null");

        if (copy == null)
            throw new IllegalArgumentException("Book copy cannot be null");

        if (dueDate == null)
            throw new IllegalArgumentException("Due date cannot be null");

        Rental rental = rentalRepo.tryRent(reader, copy, dueDate);

        if (rental == null)
            throw new IllegalStateException("Nie można wypożyczyć książki: " +
                    "kopii brak lub limit został osiągnięty.");

        // ---- Apache Kafka: wysyłamy zdarzenie o nowym wypożyczeniu ----
        if (eventProducer != null) {
            String libraryName = null;
            String libraryId = null;
            if (libraryRepo != null && copy != null && copy.getLibraryId() != null) {
                libraryId = copy.getLibraryId();
                Library lib = libraryRepo.findById(copy.getLibraryId());
                if (lib != null) {
                    libraryName = lib.getName();
                }
            }

            eventProducer.publishNewRental(rental, libraryId, libraryName);
        }
    }

    /**
     * Zwrot książki — TERAZ również transakcyjny i bezpieczny.
     */
    public void returnBook(String rentalId) {

        // 1) Pobranie wypożyczenia
        Rental rental = rentalRepo.findById(rentalId);
        if (rental == null)
            throw new IllegalArgumentException("Rental not found: " + rentalId);

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Rental is not active. Cannot return. Status = " + rental.getStatus()
            );
        }

        // 2) Pobranie kopii książki
        BookCopy copy = copyRepo.findById(rental.getBookCopyId());
        if (copy == null)
            throw new IllegalStateException("BookCopy not found: " + rental.getBookCopyId());

        // 3) Aktualizacja wypożyczenia
        rental.setStatus(RentalStatus.RETURNED);
        rental.setReturnDate(LocalDate.now());
        rentalRepo.update(rental);

        // 4) Zmiana statusu kopii
        copy.setStatus(BookStatus.AVAILABLE);
        copyRepo.update(copy);

        // 5) ⭐ Zmniejszenie licznika aktywnych wypożyczeń — ATOMICZNIE
        readerRepo.getCollection().updateOne(
                Filters.eq("_id", rental.getReaderId()),
                Updates.inc("activeRentals", -1)
        );
    }


    public List<Rental> findActiveRentalsByReader(String readerId) {
        return rentalRepo.findActiveByReader(readerId);
    }

    public Document getRentalDetails(String rentalId) {
        return rentalRepo.getRentalDetails(rentalId);
    }

    public Rental findById(String id) {
        return rentalRepo.findById(id);
    }

    public List<Rental> findActiveRentals() {
        return rentalRepo.findActiveRentals();
    }
}