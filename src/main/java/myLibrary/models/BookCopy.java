package myLibrary.models;

import myLibrary.enums.BookStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

import java.util.UUID;


@BsonDiscriminator
public class BookCopy {

    @BsonId
    private String id;

    @BsonProperty("bookId")
    private String bookId;

    @BsonProperty("libraryId")
    private String libraryId;

    @BsonRepresentation(org.bson.BsonType.STRING)
    private BookStatus status;

    public BookCopy() {}

    public BookCopy(Book book, Library library) {
        this.id = UUID.randomUUID().toString();
        this.bookId = book.getId();
        this.libraryId = library.getId();
        this.status = BookStatus.AVAILABLE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(String libraryId) {
        this.libraryId = libraryId;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BookCopy{" +
                "id='" + id + '\'' +
                ", bookId='" + bookId + '\'' +
                ", libraryId='" + libraryId + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BookCopy bookCopy = (BookCopy) o;

        return new EqualsBuilder().append(getId(), bookCopy.getId()).append(getBookId(), bookCopy.getBookId()).append(getLibraryId(), bookCopy.getLibraryId()).append(getStatus(), bookCopy.getStatus()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getBookId()).append(getLibraryId()).append(getStatus()).toHashCode();
    }
}
