import java.time.LocalDate;
import java.util.UUID;

public class Rental {
    private UUID id;
    private UUID readerId;
    private UUID bookId;
    private UUID libraryId;

    private LocalDate rentalDate;       // data wypożyczenia
    private LocalDate dueDate;          // termin zwrotu
    private LocalDate returnDate;       // faktyczny zwrot (może być null)
    private RentalStatus status;        // enum: ACTIVE, RETURNED, OVERDUE
    private double fine;                // kara za przetrzymanie

    public Rental() {
    }

    public Rental(UUID id, UUID readerId, UUID bookId, UUID libraryId,
                  LocalDate rentalDate, LocalDate dueDate) {
        this.id = id;
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
    public void setFine(double fine) { this.fine = fine; }

    @Override
    public String toString() {
        return "Rental{" +
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
