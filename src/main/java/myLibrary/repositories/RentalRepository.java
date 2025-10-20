package myLibrary.repositories;

import jakarta.persistence.EntityManager;
import myLibrary.Rental;
import myLibrary.enums.RentalStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class RentalRepository implements Repository<Rental> {

    private final EntityManager em;

    public RentalRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void add(Rental rental) {
        em.persist(rental);
    }

    @Override
    public void delete(Rental rental) {
        em.remove(rental);
    }

    @Override
    public void update(Rental rental) {
        em.merge(rental);
    }

    @Override
    public Rental find(UUID id) {
        List<Rental> rentals = em.createQuery(
                        "SELECT r FROM Rental r WHERE r.id = :id", Rental.class)
                .setParameter("id", id)
                .getResultList();
        return rentals.isEmpty() ? null : rentals.getFirst();
    }

    public List<Rental> findAll() {
        return em.createQuery("SELECT r FROM Rental r", Rental.class)
                .getResultList();
    }

    public List<Rental> findByReaderId(UUID readerId) {
        return em.createQuery(
                        "SELECT r FROM Rental r WHERE r.reader.id = :readerId", Rental.class)
                .setParameter("readerId", readerId)
                .getResultList();
    }

    public List<Rental> findByBookCopyId(UUID bookCopyId) {
        return em.createQuery(
                        "SELECT r FROM Rental r WHERE r.bookCopy.id = :bookCopyId", Rental.class)
                .setParameter("bookCopyId", bookCopyId)
                .getResultList();
    }

    public List<Rental> findByLibraryId(UUID libraryId) {
        return em.createQuery(
                        "SELECT r FROM Rental r WHERE r.bookCopy.library.id = :libraryId", Rental.class)
                .setParameter("libraryId", libraryId)
                .getResultList();
    }

    public List<Rental> findActiveRentals() {
        return em.createQuery(
                        "SELECT r FROM Rental r WHERE r.status = :status", Rental.class)
                .setParameter("status", RentalStatus.ACTIVE)
                .getResultList();
    }

    public List<Rental> findOverdueRentals() {
        return em.createQuery(
                        "SELECT r FROM Rental r WHERE r.status = :status AND r.dueDate < CURRENT_DATE", Rental.class)
                .setParameter("status", RentalStatus.ACTIVE)
                .getResultList();
    }

    public List<Rental> findByRentalDateBetween(LocalDate start, LocalDate end) {
        return em.createQuery(
                        "SELECT r FROM Rental r WHERE r.rentalDate BETWEEN :start AND :end", Rental.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    public boolean hasActiveRental(UUID readerId, UUID bookCopyId) {
        Long count = em.createQuery(
                        "SELECT COUNT(r) FROM Rental r " +
                                "WHERE r.reader.id = :readerId " +
                                "AND r.bookCopy.id = :bookCopyId " +
                                "AND r.status = :status", Long.class)
                .setParameter("readerId", readerId)
                .setParameter("bookCopyId", bookCopyId)
                .setParameter("status", RentalStatus.ACTIVE)
                .getSingleResult();
        return count > 0;
    }
}
