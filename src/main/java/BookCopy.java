import java.util.UUID;

public class BookCopy {
    private UUID id;
    private Book book;
    private UUID libraryId;
    private BookStatus status;

    public BookCopy(UUID id, Book book, UUID libraryId) {
        this.id = id;
        this.book = book;
        this.libraryId = libraryId;
        this.status = BookStatus.AVAILABLE;
    }

    public UUID getId() { return id; }
    public Book getBook() { return book; }
    public UUID getLibraryId() { return libraryId; }
    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "BookCopy{" +
                "id=" + id +
                ", book=" + book.getTitle() +
                ", libraryId=" + libraryId +
                ", status=" + status +
                '}';
    }
}
