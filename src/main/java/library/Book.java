package library;

import jakarta.persistence.*;
import library.enums.BookGenre;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


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

    public Book(String title, String author, String isbn) {
        this.title = Objects.requireNonNull(title);
        this.author = Objects.requireNonNull(author);
        this.isbn = Objects.requireNonNull(isbn);
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
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        return new EqualsBuilder().append(getPublicationYear(), book.getPublicationYear()).append(getPages(), book.getPages()).append(getId(), book.getId()).append(getTitle(), book.getTitle()).append(getAuthor(), book.getAuthor()).append(getPublisher(), book.getPublisher()).append(getGenre(), book.getGenre()).append(getIsbn(), book.getIsbn()).append(getLanguage(), book.getLanguage()).append(getDescription(), book.getDescription()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getTitle()).append(getAuthor()).append(getPublisher()).append(getGenre()).append(getIsbn()).append(getPublicationYear()).append(getPages()).append(getLanguage()).append(getDescription()).toHashCode();
    }
}
