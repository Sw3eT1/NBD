package myLibrary;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;
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

    @OneToMany(mappedBy = "readerType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookAllowedReaderType> allowedBooks = new ArrayList<>();

    @OneToMany(mappedBy = "readerType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reader> readers = new ArrayList<>();

    public ReaderType() {}

    public ReaderType(String name, int maxBooks) {
        this.name = name;
        this.maxBooks = maxBooks;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public int getMaxBooks() { return maxBooks; }

    public List<BookAllowedReaderType> getAllowedBooks() { return allowedBooks; }
    public List<Reader> getReaders() { return readers; }

    public void addReader(Reader reader) {
        if (!readers.contains(reader)) {
            readers.add(reader);
            reader.setReaderType(this);
        }
    }

    public void addAllowedBook(BookAllowedReaderType allowedBook) {
        if (!allowedBooks.contains(allowedBook)) {
            allowedBooks.add(allowedBook);
            allowedBook.setReaderType(this);
        }
    }


    @Override
    public String toString() {
        return "ReaderType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", maxBooks=" + maxBooks +
                ", allowedBooks=" + allowedBooks +
                ", readers=" + readers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ReaderType that = (ReaderType) o;

        return new EqualsBuilder().append(getMaxBooks(), that.getMaxBooks()).append(getId(), that.getId()).append(getName(), that.getName()).append(getAllowedBooks(), that.getAllowedBooks()).append(getReaders(), that.getReaders()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getName()).append(getMaxBooks()).append(getAllowedBooks()).append(getReaders()).toHashCode();
    }
}
