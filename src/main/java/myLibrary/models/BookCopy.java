package myLibrary.models;

import com.datastax.oss.driver.api.mapper.annotations.*;
import myLibrary.enums.BookStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

@Entity(defaultKeyspace = "library")
@CqlName("book_copies_by_library")
public class BookCopy {

    // Uwaga: tu id staje się częścią key (clustering), żeby uniknąć kolizji
    @ClusteringColumn(2)
    @CqlName("copy_id")
    private String id;

    @PartitionKey
    @CqlName("library_id")
    private String libraryId;

    @ClusteringColumn(1)
    @CqlName("book_id")
    private String bookId;

    @CqlName("status")
    private String status;

    public BookCopy() {
        this.id = UUID.randomUUID().toString();
    }

    public BookCopy(Book book, Library library) {
        this.id = UUID.randomUUID().toString();
        this.bookId = book.getId();
        this.libraryId = library.getId();
        this.status = BookStatus.AVAILABLE.toString();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getLibraryId() { return libraryId; }
    public void setLibraryId(String libraryId) { this.libraryId = libraryId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Transient
    public BookStatus getStatusEnum() {
        return status == null ? null : BookStatus.valueOf(status);
    }

    @Transient
    public void setStatusEnum(BookStatus status) {
        this.status = status == null ? null : status.toString();
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

        return new EqualsBuilder()
                .append(getId(), bookCopy.getId())
                .append(getBookId(), bookCopy.getBookId())
                .append(getLibraryId(), bookCopy.getLibraryId())
                .append(getStatus(), bookCopy.getStatus())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getBookId())
                .append(getLibraryId())
                .append(getStatus())
                .toHashCode();
    }
}