package library;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "reader_types")
public abstract class ReaderType {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    protected String name;

    @Column(nullable = false)
    protected int maxBooks;


    public ReaderType() {}

    public ReaderType(String name, int maxBooks) {
        this.name = name;
        this.maxBooks = maxBooks;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public int getMaxBooks() { return maxBooks; }
}
