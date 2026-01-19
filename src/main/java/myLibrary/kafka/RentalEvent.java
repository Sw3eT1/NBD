package myLibrary.kafka;

import myLibrary.models.Rental;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Komunikat wysyłany do tematu Kafka dotyczący nowego wypożyczenia.
 *
 * Format: JSON (serializacja JSON-B).
 */
public class RentalEvent {

    /**
     * Unikalny identyfikator zdarzenia (do idempotentnego zapisu po stronie konsumenta).
     */
    private String eventId;

    /**
     * Identyfikator wypożyczenia w bazie.
     */
    private String rentalId;

    private String readerId;
    private String bookCopyId;
    private String libraryId;
    private String libraryName;
    private String status;

    private LocalDate rentalDate;
    private LocalDate dueDate;

    /**
     * Czas utworzenia zdarzenia (UTC).
     */
    private Instant createdAt;

    public RentalEvent() {
        // JSON-B
    }

    public static RentalEvent from(Rental rental, String libraryId, String libraryName) {
        RentalEvent e = new RentalEvent();
        e.eventId = UUID.randomUUID().toString();
        e.rentalId = rental.getId();
        e.readerId = rental.getReaderId();
        e.bookCopyId = rental.getBookCopyId();
        e.libraryId = libraryId;
        e.libraryName = libraryName;
        e.status = rental.getStatus().toString();
        e.rentalDate = rental.getRentalDate();
        e.dueDate = rental.getDueDate();
        e.createdAt = Instant.now();
        return e;
    }

    // --- getters / setters ---

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getRentalId() { return rentalId; }
    public void setRentalId(String rentalId) { this.rentalId = rentalId; }

    public String getReaderId() { return readerId; }
    public void setReaderId(String readerId) { this.readerId = readerId; }

    public String getBookCopyId() { return bookCopyId; }
    public void setBookCopyId(String bookCopyId) { this.bookCopyId = bookCopyId; }

    public String getLibraryId() { return libraryId; }
    public void setLibraryId(String libraryId) { this.libraryId = libraryId; }

    public String getLibraryName() { return libraryName; }
    public void setLibraryName(String libraryName) { this.libraryName = libraryName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getRentalDate() { return rentalDate; }
    public void setRentalDate(LocalDate rentalDate) { this.rentalDate = rentalDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
