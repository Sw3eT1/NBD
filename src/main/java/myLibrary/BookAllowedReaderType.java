package myLibrary;

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

    public void setReaderType(ReaderType readerType) {
        this.readerType = readerType;
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

        return new EqualsBuilder().append(getId(), that.getId()).append(getBook(), that.getBook()).append(getReaderType(), that.getReaderType()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getBook()).append(getReaderType()).toHashCode();
    }
}