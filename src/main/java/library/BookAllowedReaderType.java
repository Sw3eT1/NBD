package library;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

@Entity
@Table(name = "book_allowed_reader_type")
public class BookAllowedReaderType {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "reader_type_id")
    private ReaderType readerType;

    public BookAllowedReaderType() {}
    public BookAllowedReaderType(Book book, ReaderType readerType) {
        this.book = book;
        this.readerType = readerType;
    }

    public UUID getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }


    public ReaderType getReaderType() {
        return readerType;
    }

    @Override
    public String toString() {
        return "BookAllowedReaderType{" +
                "id=" + id +
                ", book=" + book +
                ", readerType=" + readerType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BookAllowedReaderType that = (BookAllowedReaderType) o;

        return new EqualsBuilder().append(id, that.id).append(book, that.book).append(readerType, that.readerType).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(book).append(readerType).toHashCode();
    }
}