package myLibrary.services;

import myLibrary.enums.BookStatus;
import myLibrary.enums.RentalStatus;
import myLibrary.models.BookCopy;
import myLibrary.models.Reader;
import myLibrary.models.Rental;
import myLibrary.repositories.BookCopyRepository;
import myLibrary.repositories.RentalRepository;
import org.bson.Document;

import java.time.LocalDate;
import java.util.List;

public class RentalService {

    private final RentalRepository rentalRepo;
    private final BookCopyRepository copyRepo;

    public RentalService(RentalRepository rentalRepo, BookCopyRepository copyRepo) {
        this.rentalRepo = rentalRepo;
        this.copyRepo = copyRepo;
    }

    public void rent(Reader reader, BookCopy copy, LocalDate dueDate) {

        if (rentalRepo.hasActiveRental(reader.getId(), copy.getId()))
            throw new IllegalStateException("Egzemplarz już wypożyczony.");

        if (copy.getStatus() != BookStatus.AVAILABLE)
            throw new IllegalStateException("Egzemplarz niedostępny.");

        copy.setStatus(BookStatus.RENTED);
        copyRepo.update(copy);

        Rental rental = new Rental(reader, copy, LocalDate.now(), dueDate);
        rentalRepo.insert(rental);
    }

    public void returnBook(String rentalId) {
        Rental rental = rentalRepo.findById(rentalId);

        rental.setStatus(RentalStatus.RETURNED);
        rental.setReturnDate(LocalDate.now());
        rentalRepo.update(rental);

        BookCopy copy = copyRepo.findById(rental.getBookCopyId());
        copy.setStatus(BookStatus.AVAILABLE);
        copyRepo.update(copy);
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


