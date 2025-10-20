package myLibrary;

import jakarta.persistence.*;
import myLibrary.enums.BookGenre;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name = "Books")
public class Book {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "title",  nullable = false)
    private String title;

    @Column(name = "author",  nullable = false)
    private String author;


    private String publisher;

    @Enumerated(EnumType.STRING)
    private BookGenre genre;

    @Column(name = "isbn",  nullable = false)
    private String isbn;

    private int publicationYear;
    private int pages;
    private String language;
    private String description;

    @Version
    private long version;


    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BookCopy> copies = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookAllowedReaderType> allowedReaderTypes;

    public Book(String title, String author, String isbn, BookGenre genre) {
        this.title = Objects.requireNonNull(title);
        this.author = Objects.requireNonNull(author);
        this.isbn = Objects.requireNonNull(isbn);
        this.copies = new ArrayList<>();
        this.allowedReaderTypes = new ArrayList<>();
        this.genre = genre;
    }

    public Book() {

    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public BookGenre getGenre() {
        return genre;
    }

    public void setGenre(BookGenre genre) {
        this.genre = genre;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addCopy(BookCopy copy) {
        if (!copies.contains(copy)) {
            copies.add(copy);
            copy.setBook(this);
        }
    }

    public void removeCopy(BookCopy copy) {
        if (copies.remove(copy)) {
            copy.setBook(null);
        }
    }

    public void addAllowedType(ReaderType type) {
        boolean exists = allowedReaderTypes.stream()
                .anyMatch(art -> art.getReaderType().equals(type));
        if (!exists) {
            BookAllowedReaderType relation = new BookAllowedReaderType(this, type);
            allowedReaderTypes.add(relation);
            type.addAllowedBook(relation);
        }
    }

    public void removeAllowedType(ReaderType type) {
        allowedReaderTypes.removeIf(art -> art.getReaderType().equals(type));
    }


    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", genre=" + genre +
                ", isbn='" + isbn + '\'' +
                ", publicationYear=" + publicationYear +
                ", pages=" + pages +
                ", language='" + language + '\'' +
                ", description='" + description + '\'' +
                ", copies=" + copies +
                ", allowedReaderTypes=" + allowedReaderTypes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return publicationYear == book.publicationYear && pages == book.pages && version == book.version && Objects.equals(id, book.id) && Objects.equals(title, book.title) && Objects.equals(author, book.author) && Objects.equals(publisher, book.publisher) && genre == book.genre && Objects.equals(isbn, book.isbn) && Objects.equals(language, book.language) && Objects.equals(description, book.description) && Objects.equals(copies, book.copies) && Objects.equals(allowedReaderTypes, book.allowedReaderTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, publisher, genre, isbn, publicationYear, pages, language, description, version, copies, allowedReaderTypes);
    }
}
