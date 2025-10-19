package library;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "reader_types")
public abstract class ReaderType {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    protected String name;

    @Column(nullable = false)
    protected int maxBooks;

    @OneToMany(mappedBy = "readerType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookAllowedReaderType> allowedBooks = new ArrayList<>();

    public ReaderType() {}

    public ReaderType(String name, int maxBooks) {
        this.name = name;
        this.maxBooks = maxBooks;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public int getMaxBooks() { return maxBooks; }
    public List<BookAllowedReaderType> getAllowedBooks() { return allowedBooks; }
}
