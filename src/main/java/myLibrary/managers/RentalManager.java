package myLibrary.managers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import myLibrary.BookCopy;
import myLibrary.Reader;
import myLibrary.Rental;
import myLibrary.enums.BookStatus;
import myLibrary.enums.RentalStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class RentalManager extends BaseManager {

    public RentalManager(EntityManager em) {
        super(em);
    }

    public void rentBook(Reader reader, BookCopy copy, LocalDate dueDate) {
        executeInTransaction(() -> {
            Reader managedReader = em.contains(reader) ? reader : em.merge(reader);
            BookCopy managedCopy = em.contains(copy) ? copy : em.merge(copy);

            if (managedCopy.getStatus() != BookStatus.AVAILABLE) {
                throw new IllegalStateException("Egzemplarz nie jest dostępny do wypożyczenia.");
            }

            if (hasActiveRental(managedReader.getId(), managedCopy.getId())) {
                throw new IllegalStateException("Czytelnik ma już aktywne wypożyczenie tej książki.");
            }

            if (countActiveRentals(managedReader) >= managedReader.getReaderType().getMaxBooks()) {
                throw new IllegalStateException("Czytelnik osiągnął maksymalny limit wypożyczeń.");
            }

            Rental rental = new Rental(managedReader, managedCopy, LocalDate.now(), dueDate);
            managedCopy.setStatus(BookStatus.RENTED);

            em.persist(rental);
            em.merge(managedCopy);
        });
    }

    public void returnBook(UUID rentalId) {
        executeInTransaction(() -> {
            Rental rental = em.find(Rental.class, rentalId);
            if (rental == null) {
                throw new IllegalArgumentException("Nie znaleziono wypożyczenia o ID: " + rentalId);
            }

            if (rental.getStatus() != RentalStatus.ACTIVE) {
                throw new IllegalStateException("To wypożyczenie nie jest aktywne.");
            }

            rental.setReturnDate(LocalDate.now());
            rental.setStatus(RentalStatus.RETURNED);

            BookCopy copy = rental.getBookCopy();
            copy.setStatus(BookStatus.AVAILABLE);

            em.merge(rental);
            em.merge(copy);
        });
    }

    private boolean hasActiveRental(UUID readerId, UUID copyId) {
        TypedQuery<Long> query = em.createQuery("""
            SELECT COUNT(r) FROM Rental r
            WHERE r.reader.id = :readerId
              AND r.bookCopy.id = :copyId
              AND r.status = :status
        """, Long.class);
        query.setParameter("readerId", readerId);
        query.setParameter("copyId", copyId);
        query.setParameter("status", RentalStatus.ACTIVE);
        return query.getSingleResult() > 0;
    }

    private long countActiveRentals(Reader reader) {
        TypedQuery<Long> query = em.createQuery("""
            SELECT COUNT(r) FROM Rental r
            WHERE r.reader.id = :readerId
              AND r.status = :status
        """, Long.class);
        query.setParameter("readerId", reader.getId());
        query.setParameter("status", RentalStatus.ACTIVE);
        return query.getSingleResult();
    }

    public List<Rental> findActiveRentals() {
        return em.createQuery("""
            SELECT r FROM Rental r
            WHERE r.status = :status
        """, Rental.class)
                .setParameter("status", RentalStatus.ACTIVE)
                .getResultList();
    }
}
