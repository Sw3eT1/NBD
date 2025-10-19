package library;

import jakarta.persistence.*;
import library.enums.BookStatus;

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

    public BookCopy(Book book, Library library) {
        this.book = book;
        this.library = library;
        this.status = BookStatus.AVAILABLE;
    }

    public BookCopy() {

    }

    public UUID getId() { return id; }
    public Book getBook() { return book; }
    public Library getLibrary() { return library; }
    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "library.BookCopy{" +
                "id=" + id +
                ", book=" + book.getTitle() +
                ", libraryId=" + library.getId() +
                ", status=" + status +
                '}';
    }
}
