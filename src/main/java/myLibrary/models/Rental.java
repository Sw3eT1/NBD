package myLibrary.models;

import myLibrary.enums.RentalStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

import java.time.LocalDate;
import java.util.UUID;

@BsonDiscriminator
public class Rental {

    @BsonId
    private String id;

    @BsonProperty("readerId")
    private String readerId;

    @BsonProperty("bookCopyId")
    private String bookCopyId;

    private LocalDate rentalDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    private RentalStatus status;

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
        this.status = RentalStatus.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public String getBookCopyId() {
        return bookCopyId;
    }

    public void setBookCopyId(String bookCopyId) {
        this.bookCopyId = bookCopyId;
    }

    public LocalDate getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(LocalDate rentalDate) {
        this.rentalDate = rentalDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

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

        return new EqualsBuilder().append(getFine(), rental.getFine()).append(getId(), rental.getId()).append(getReaderId(), rental.getReaderId()).append(getBookCopyId(), rental.getBookCopyId()).append(getRentalDate(), rental.getRentalDate()).append(getDueDate(), rental.getDueDate()).append(getReturnDate(), rental.getReturnDate()).append(getStatus(), rental.getStatus()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getReaderId()).append(getBookCopyId()).append(getRentalDate()).append(getDueDate()).append(getReturnDate()).append(getStatus()).append(getFine()).toHashCode();
    }
}
