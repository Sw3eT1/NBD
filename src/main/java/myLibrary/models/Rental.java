package myLibrary.models;

import com.datastax.oss.driver.api.mapper.annotations.*;
import myLibrary.enums.RentalStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;
import java.util.UUID;

@Entity(defaultKeyspace = "library")
@CqlName("rentals_by_reader")
public class Rental {

    @ClusteringColumn
    @CqlName("rental_id")
    private String id;

    @PartitionKey
    @CqlName("reader_id")
    private String readerId;

    @CqlName("book_copy_id")
    private String bookCopyId;

    @CqlName("rental_date")
    private LocalDate rentalDate;

    @CqlName("due_date")
    private LocalDate dueDate;

    @CqlName("return_date")
    private LocalDate returnDate;

    @CqlName("status")
    private String status;

    @CqlName("fine")
    private double fine;

    public Rental() {
        this.id = UUID.randomUUID().toString();
    }

    public Rental(Reader reader, BookCopy copy,
                  LocalDate rentalDate, LocalDate dueDate) {

        this.id = UUID.randomUUID().toString();
        this.readerId = reader.getId();
        this.bookCopyId = copy.getId();
        this.rentalDate = rentalDate;
        this.dueDate = dueDate;
        this.status = RentalStatus.ACTIVE.toString();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getReaderId() { return readerId; }
    public void setReaderId(String readerId) { this.readerId = readerId; }

    public String getBookCopyId() { return bookCopyId; }
    public void setBookCopyId(String bookCopyId) { this.bookCopyId = bookCopyId; }

    public LocalDate getRentalDate() { return rentalDate; }
    public void setRentalDate(LocalDate rentalDate) { this.rentalDate = rentalDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Transient
    public RentalStatus getStatusEnum() {
        return status == null ? null : RentalStatus.valueOf(status);
    }

    @Transient
    public void setStatusEnum (RentalStatus s) {
        this.status = s == null ? null : s.name();
    }

    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }

    @Override
    public String toString() {
        return "Rental{" +
                "id='" + id + '\'' +
                ", readerId='" + readerId + '\'' +
                ", bookCopyId='" + bookCopyId + '\'' +
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

        return new EqualsBuilder()
                .append(getFine(), rental.getFine())
                .append(getId(), rental.getId())
                .append(getReaderId(), rental.getReaderId())
                .append(getBookCopyId(), rental.getBookCopyId())
                .append(getRentalDate(), rental.getRentalDate())
                .append(getDueDate(), rental.getDueDate())
                .append(getReturnDate(), rental.getReturnDate())
                .append(getStatus(), rental.getStatus())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getReaderId())
                .append(getBookCopyId())
                .append(getRentalDate())
                .append(getDueDate())
                .append(getReturnDate())
                .append(getStatus())
                .append(getFine())
                .toHashCode();
    }
}