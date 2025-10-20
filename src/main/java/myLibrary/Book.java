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
        this.copies.add(copy);
        copy.setBook(this);
    }

    public void removeCopy(BookCopy copy) {
        this.copies.remove(copy);
        copy.setBook(null);
    }

    public List<BookCopy> getCopies() {
        return copies;
    }


    public void addAllowedType(ReaderType type) {
        BookAllowedReaderType bar = new BookAllowedReaderType(this, type);
        allowedReaderTypes.add(bar);
    }

    public void removeAllowedType(ReaderType type) {
        allowedReaderTypes.removeIf(bar -> bar.getReaderType().equals(type));
    }

    public List<BookAllowedReaderType> getAllowedTypes() {
        return allowedReaderTypes;
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
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        return new EqualsBuilder().append(getPublicationYear(), book.getPublicationYear()).append(getPages(), book.getPages()).append(getId(), book.getId()).append(getTitle(), book.getTitle()).append(getAuthor(), book.getAuthor()).append(getPublisher(), book.getPublisher()).append(getGenre(), book.getGenre()).append(getIsbn(), book.getIsbn()).append(getLanguage(), book.getLanguage()).append(getDescription(), book.getDescription()).append(getCopies(), book.getCopies()).append(allowedReaderTypes, book.allowedReaderTypes).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getTitle()).append(getAuthor()).append(getPublisher()).append(getGenre()).append(getIsbn()).append(getPublicationYear()).append(getPages()).append(getLanguage()).append(getDescription()).append(getCopies()).append(allowedReaderTypes).toHashCode();
    }
}
