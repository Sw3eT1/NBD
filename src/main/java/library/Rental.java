package library;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import library.enums.RentalStatus;

import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name = "Rentals")

public class Rental {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Column(name= "rented_by", nullable = false)
    private UUID readerId;

    @NotNull
    @Column(name= "rented_book", nullable = false)
    private UUID bookId;

    @NotNull
    @Column(name= "rented_in", nullable = false)
    private UUID libraryId;


    @NotNull
    @Column(name= "rental_start", nullable = false)
    private LocalDate rentalDate;

    @NotNull
    @Column(name= "rental_end", nullable = false)
    private LocalDate dueDate;

    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    private RentalStatus status;

    private double fine;

    public Rental() {
    }

    public Rental(UUID readerId, UUID bookId, UUID libraryId,
                  LocalDate rentalDate, LocalDate dueDate) {
        this.readerId = readerId;
        this.bookId = bookId;
        this.libraryId = libraryId;
        this.rentalDate = rentalDate;
        this.dueDate = dueDate;
        this.status = RentalStatus.ACTIVE;
        this.fine = 0.0;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getReaderId() { return readerId; }
    public void setReaderId(UUID readerId) { this.readerId = readerId; }

    public UUID getBookId() { return bookId; }
    public void setBookId(UUID bookId) { this.bookId = bookId; }

    public UUID getLibraryId() { return libraryId; }
    public void setLibraryId(UUID libraryId) { this.libraryId = libraryId; }

    public LocalDate getRentalDate() { return rentalDate; }
    public void setRentalDate(LocalDate rentalDate) { this.rentalDate = rentalDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public RentalStatus getStatus() { return status; }
    public void setStatus(RentalStatus status) { this.status = status; }

    public double getFine() { return fine; }

    @Override
    public String toString() {
        return "library.Rental{" +
                "id=" + id +
                ", readerId=" + readerId +
                ", bookId=" + bookId +
                ", libraryId=" + libraryId +
                ", rentalDate=" + rentalDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", status=" + status +
                ", fine=" + fine +
                '}';
    }
}
