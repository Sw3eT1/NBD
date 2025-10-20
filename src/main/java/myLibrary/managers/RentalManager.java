package myLibrary.managers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import myLibrary.BookCopy;
import myLibrary.Reader;
import myLibrary.Rental;
import myLibrary.enums.BookStatus;
import myLibrary.enums.RentalStatus;
import myLibrary.repositories.RentalRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class RentalManager {
    private final EntityManager entityManager;
    private final RentalRepository rentalRepository;

    public RentalManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.rentalRepository = new RentalRepository(entityManager);
    }

    public void rentBook(Reader reader, BookCopy copy, LocalDate dueDate) {
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();

            if (copy.getId() == null) {
                entityManager.persist(copy);
            }

            if (copy.getStatus() != BookStatus.AVAILABLE) {
                throw new IllegalStateException("Egzemplarz nie jest dostępny!");
            }
            if (rentalRepository.hasActiveRental(reader.getId(), copy.getId())) {
                throw new IllegalStateException("Czytelnik ma już aktywne wypożyczenie tego egzemplarza!");
            }

            Rental rental = new Rental(reader, copy, LocalDate.now(), dueDate);
            copy.setStatus(BookStatus.RENTED);
            copy.addRental(rental);
            reader.addRental(rental);
            rentalRepository.add(rental);

            tx.commit();
            System.out.println("Wypożyczono książkę: " + copy.getBook().getTitle());
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void returnBook(UUID rentalId) {
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();

            Rental rental = rentalRepository.find(rentalId);
            if (rental == null) {
                throw new IllegalArgumentException("Nie znaleziono wypożyczenia!");
            }
            if (rental.getStatus() != RentalStatus.ACTIVE) {
                throw new IllegalStateException("To wypożyczenie nie jest aktywne!");
            }

            rental.setStatus(RentalStatus.RETURNED);
            rental.setReturnDate(LocalDate.now());

            BookCopy copy = rental.getBookCopy();
            copy.setStatus(BookStatus.AVAILABLE);

            entityManager.merge(copy);
            rentalRepository.update(rental);

            tx.commit();
            System.out.println("Zwrócono książkę: " + rental.getBookCopy().getBook().getTitle());
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public List<Rental> findActiveRentals() {
        return rentalRepository.findActiveRentals();
    }
}
