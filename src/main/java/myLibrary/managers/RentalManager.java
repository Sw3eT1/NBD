package myLibrary.managers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import myLibrary.BookCopy;
import myLibrary.Reader;
import myLibrary.Rental;
import myLibrary.enums.BookStatus;
import myLibrary.enums.RentalStatus;
import myLibrary.repositories.RentalRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class RentalManager extends BaseManager {
    private final RentalRepository rentalRepository;

    public RentalManager(EntityManager entityManager) {
        super(entityManager);
        this.rentalRepository = new RentalRepository(entityManager);
    }

    public void rentBook(Reader reader, BookCopy copy, LocalDate dueDate) {
        executeInTransaction(() -> {
            BookCopy lockedCopy = em.find(BookCopy.class, copy.getId(), LockModeType.OPTIMISTIC);

            if (lockedCopy.getStatus() != BookStatus.AVAILABLE) {
                throw new IllegalStateException("Egzemplarz nie jest dostępny!");
            }

            long activeCount = rentalRepository.findByReaderId(reader.getId()).stream()
                    .filter(r -> r.getStatus() == RentalStatus.ACTIVE)
                    .count();

            if (activeCount >= reader.getReaderType().getMaxBooks()) {
                throw new IllegalStateException("Czytelnik osiągnął limit wypożyczeń!");
            }

            Rental rental = new Rental(reader, lockedCopy, LocalDate.now(), dueDate);
            lockedCopy.setStatus(BookStatus.RENTED);
            reader.addRental(rental);
            lockedCopy.addRental(rental);
            rentalRepository.add(rental);
        });
    }

    public void returnBook(UUID rentalId) {
        executeInTransaction(() -> {
            Rental rental = rentalRepository.find(rentalId);
            if (rental == null)
                throw new IllegalArgumentException("Nie znaleziono wypożyczenia!");
            if (rental.getStatus() != RentalStatus.ACTIVE)
                throw new IllegalStateException("To wypożyczenie nie jest aktywne!");

            rental.markReturned();
            BookCopy copy = rental.getBookCopy();
            copy.setStatus(BookStatus.AVAILABLE);
            em.merge(copy);
            rentalRepository.update(rental);
        });
    }

    public List<Rental> findActiveRentals() {
        return rentalRepository.findActiveRentals();
    }
}

