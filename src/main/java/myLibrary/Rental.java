package myLibrary;

import jakarta.persistence.*;
import myLibrary.enums.RentalStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name = "Rentals")
public class Rental {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "reader_id", nullable = false)
    private Reader reader;

    @ManyToOne
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopy bookCopy;

    private LocalDate rentalDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus status;

    private double fine;

    public Rental() {}

    public Rental(Reader reader, BookCopy bookCopy,
                  LocalDate rentalDate, LocalDate dueDate) {
        this.reader = reader;
        this.bookCopy = bookCopy;
        this.rentalDate = rentalDate;
        this.dueDate = dueDate;
        this.status = RentalStatus.ACTIVE;
        this.fine = 0.0;
    }

    public UUID getId() { return id; }
    public Reader getReader() { return reader; }
    public void setReader(Reader reader) { this.reader = reader; }
    public BookCopy getBookCopy() { return bookCopy; }
    public void setBookCopy(BookCopy bookCopy) { this.bookCopy = bookCopy; }
    public LocalDate getRentalDate() { return rentalDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate date) { this.returnDate = date;}
    public RentalStatus getStatus() { return status; }
    public void setStatus(RentalStatus status) { this.status = status; }
    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }

    public void markReturned() {
        this.status = RentalStatus.RETURNED;
        this.returnDate = LocalDate.now();
    }

    @Override
    public String toString() {
        return "Rental{" +
                "id=" + id +
                ", reader=" + reader +
                ", bookCopy=" + bookCopy +
                ", rentalDate=" + rentalDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", status=" + status +
                ", fine=" + fine +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Rental rental = (Rental) o;

        return new EqualsBuilder().append(getFine(), rental.getFine()).append(getId(), rental.getId()).append(getReader(), rental.getReader()).append(getBookCopy(), rental.getBookCopy()).append(getRentalDate(), rental.getRentalDate()).append(getDueDate(), rental.getDueDate()).append(getReturnDate(), rental.getReturnDate()).append(getStatus(), rental.getStatus()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getReader()).append(getBookCopy()).append(getRentalDate()).append(getDueDate()).append(getReturnDate()).append(getStatus()).append(getFine()).toHashCode();
    }
}
