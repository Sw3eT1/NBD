package myLibrary;

import jakarta.persistence.*;
import myLibrary.enums.BookStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "book_copies")
public class BookCopy {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "library_id")
    private Library library;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @Version
    private long version;


    @OneToMany(mappedBy = "bookCopy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rental> rentals = new ArrayList<>();

    public BookCopy(Book book, Library library) {
        this.book = book;
        this.library = library;
        this.status = BookStatus.AVAILABLE;
    }

    public BookCopy() {}

    public UUID getId() { return id; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public Library getLibrary() { return library; }
    public void setLibrary(Library library) { this.library = library; }
    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }
    public List<Rental> getRentals() { return rentals; }

    public void addRental(Rental rental) {
        if (!rentals.contains(rental)) {
            rentals.add(rental);
            rental.setBookCopy(this);
        }
    }

    public void removeRental(Rental rental) {
        if (rentals.remove(rental)) {
            rental.setBookCopy(null);
        }
    }


    @Override
    public String toString() {
        return "BookCopy{" +
                "id=" + id +
                ", book=" + book +
                ", library=" + library +
                ", status=" + status +
                ", rentals=" + rentals +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BookCopy bookCopy = (BookCopy) o;

        return new EqualsBuilder().append(getId(), bookCopy.getId()).append(getBook(), bookCopy.getBook()).append(getLibrary(), bookCopy.getLibrary()).append(getStatus(), bookCopy.getStatus()).append(getRentals(), bookCopy.getRentals()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getBook()).append(getLibrary()).append(getStatus()).append(getRentals()).toHashCode();
    }
}
