package library.repositories;

import jakarta.persistence.EntityManager;
import library.Employee;
import library.Rental;
import library.enums.RentalStatus;

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
        List<Rental> rentals = em.createQuery("SELECT r from Rental r where r.id = :id", Rental.class)
                .setParameter("id", id).
                getResultList();
        return rentals.isEmpty() ? null : rentals.getFirst();
    }

    public List<Rental> findAll() {
        return em.createQuery("SELECT r FROM Rental r", Rental.class)
                .getResultList();
    }

    public List<Rental> findByReaderId(UUID readerId) {
        return em.createQuery("SELECT r FROM Rental r WHERE r.readerId = :readerId", Rental.class)
                .setParameter("readerId", readerId)
                .getResultList();
    }

    public List<Rental> findByBookId(UUID bookId) {
        return em.createQuery("SELECT r FROM Rental r WHERE r.bookId = :bookId", Rental.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    public List<Rental> findByLibraryId(UUID libraryId) {
        return em.createQuery("SELECT r FROM Rental r WHERE r.libraryId = :libraryId", Rental.class)
                .setParameter("libraryId", libraryId)
                .getResultList();
    }

    public List<Rental> findActiveRentals() {
        return em.createQuery("SELECT r FROM Rental r WHERE r.status = :status", Rental.class)
                .setParameter("status", RentalStatus.ACTIVE)
                .getResultList();
    }

    public List<Rental> findOverdueRentals() {
        return em.createQuery("SELECT r FROM Rental r WHERE r.status = :status AND r.dueDate < CURRENT_DATE", Rental.class)
                .setParameter("status", RentalStatus.ACTIVE)
                .getResultList();
    }

    public List<Rental> findByRentalDateBetween(LocalDate start, LocalDate end) {
        return em.createQuery("SELECT r FROM Rental r WHERE r.rentalDate BETWEEN :start AND :end", Rental.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    public boolean hasActiveRental(UUID readerId, UUID bookId) {
        Long count = em.createQuery(
                        "SELECT COUNT(r) FROM Rental r WHERE r.readerId = :readerId AND r.bookId = :bookId AND r.status = :status", Long.class)
                .setParameter("readerId", readerId)
                .setParameter("bookId", bookId)
                .setParameter("status", RentalStatus.ACTIVE)
                .getSingleResult();
        return count > 0;
    }



}
