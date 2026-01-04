package myLibrary.tests;

import myLibrary.enums.BookGenre;
import myLibrary.enums.BookStatus;
import myLibrary.enums.RentalStatus;
import myLibrary.models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class RepositoriesCrudTest extends CassandraTestBase {

    @Test
    @Order(1)
    void bookRepositoryCrud() {
        Book book = new Book("Test Book", "Author", "ISBN-1", BookGenre.CLASSIC);
        book.setDescription("desc");

        // CREATE
        bookRepo.insert(book);

        // READ
        Book fromDb = bookRepo.findById(book.getId());
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals(book.getTitle(), fromDb.getTitle());

        // UPDATE
        book.setDescription("updated");
        bookRepo.update(book);

        Book updated = bookRepo.findById(book.getId());
        Assertions.assertEquals("updated", updated.getDescription());

        // DELETE
        bookRepo.delete(book.getId());
        Book deleted = bookRepo.findById(book.getId());
        Assertions.assertNull(deleted);
    }

    @Test
    @Order(2)
    void libraryRepositoryCrud() {
        Address addr = new Address("1", "Main", "City", "State", "00-000", "Country");
        Library library = new Library("Lib", addr, "111", "lib@mail.com", "site", true, "8-16");

        libraryRepo.insert(library);
        Library fromDb = libraryRepo.findById(library.getId());
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals("Lib", fromDb.getName());

        library.setOpeningHours("9-17");
        libraryRepo.update(library);
        Library updated = libraryRepo.findById(library.getId());
        Assertions.assertEquals("9-17", updated.getOpeningHours());

        libraryRepo.delete(library.getId());
        Assertions.assertNull(libraryRepo.findById(library.getId()));
    }

    @Test
    @Order(3)
    void readerTypeRepositoryCrud() {
        ReaderTypeAdult adult = new ReaderTypeAdult();

        readerTypeRepo.insert(adult);
        ReaderType fromDb = readerTypeRepo.findById(adult.getId());
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals("ADULT", fromDb.getName());

        fromDb.setMaxBooks(20);
        readerTypeRepo.update(fromDb);

        ReaderType updated = readerTypeRepo.findById(adult.getId());
        Assertions.assertEquals(20, updated.getMaxBooks());

        readerTypeRepo.delete(adult.getId());
        Assertions.assertNull(readerTypeRepo.findById(adult.getId()));
    }

    @Test
    @Order(4)
    void readerRepositoryCrud() {
        Address addr = new Address("2", "Street", "City", "State", "00-001", "Country");
        Library library = new Library("Lib2", addr, "222", "l2@mail.com", "site2", false, "10-18");
        libraryRepo.insert(library);

        ReaderTypeTeenager teenager = new ReaderTypeTeenager();
        readerTypeRepo.insert(teenager);

        Reader reader = new Reader("Jan", "Kowalski", "jan@mail.com", "123",
                addr, library, "CARD-1", teenager);

        readerRepo.insert(reader);

        Reader fromDb = readerRepo.findById(library.getId(), reader.getId());
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals("Jan", fromDb.getName());

        reader.setActiveRentals(3);
        readerRepo.update(reader);

        Reader updated = readerRepo.findById(library.getId(), reader.getId());
        Assertions.assertEquals(3, updated.getActiveRentals());

        readerRepo.delete(library.getId(), reader.getId());
        Assertions.assertNull(readerRepo.findById(library.getId(), reader.getId()));
    }

    @Test
    @Order(5)
    void employeeRepositoryCrud() {
        Address addr = new Address("3", "Street", "City", "State", "00-002", "Country");
        Library library = new Library("Lib3", addr, "333", "l3@mail.com", "site3", true, "9-19");
        libraryRepo.insert(library);

        Employee emp = new Employee("Ewa", "Nowak", "ewa@mail.com", "555",
                addr, library, "Librarian", 3000, LocalDate.of(2020, 1, 1));

        employeeRepo.insert(emp);

        Employee fromDb = employeeRepo.findById(library.getId(), emp.getId());
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals("Ewa", fromDb.getName());

        emp.setSalary(3500);
        employeeRepo.update(emp);
        Employee updated = employeeRepo.findById(library.getId(), emp.getId());
        Assertions.assertEquals(3500, updated.getSalary());

        employeeRepo.delete(library.getId(), emp.getId());
        Assertions.assertNull(employeeRepo.findById(library.getId(), emp.getId()));
    }

    @Test
    @Order(6)
    void bookCopyRepositoryCrud() {
        Address addr = new Address("4", "Street", "City", "State", "00-003", "Country");
        Library library = new Library("Lib4", addr, "444", "l4@mail.com", "site4", true, "8-18");
        libraryRepo.insert(library);

        Book book = new Book("CopyBook", "Author", "COPY-1", BookGenre.SCIENCE_FICTION);
        bookRepo.insert(book);

        BookCopy copy = new BookCopy(book, library);
        copy.setStatus(BookStatus.AVAILABLE);
        copyRepo.insert(copy);

        BookCopy fromDb = copyRepo.findById(library.getId(), book.getId(), copy.getId());
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals(BookStatus.AVAILABLE, fromDb.getStatus());

        copy.setStatus(BookStatus.RENTED);
        copyRepo.update(copy);
        BookCopy updated = copyRepo.findById(library.getId(), book.getId(), copy.getId());
        Assertions.assertEquals(BookStatus.RENTED, updated.getStatus());

        copyRepo.delete(library.getId(), book.getId(), copy.getId());
        Assertions.assertNull(copyRepo.findById(library.getId(), book.getId(), copy.getId()));
    }

    @Test
    @Order(7)
    void rentalRepositoryCrud() {
        Address addr = new Address("5", "Street", "City", "State", "00-004", "Country");
        Library library = new Library("Lib5", addr, "555", "l5@mail.com", "site5", true, "7-17");
        libraryRepo.insert(library);

        ReaderTypeKid kid = new ReaderTypeKid();
        readerTypeRepo.insert(kid);

        Reader reader = new Reader("Ola", "Test", "ola@mail.com", "777",
                addr, library, "CARD-2", kid);
        readerRepo.insert(reader);

        Book book = new Book("RentalBook", "Author", "REN-1", BookGenre.SCIENCE_FICTION);
        bookRepo.insert(book);

        BookCopy copy = new BookCopy(book, library);
        copy.setStatus(BookStatus.AVAILABLE);
        copyRepo.insert(copy);

        Rental rental = new Rental(reader, copy, LocalDate.now(), LocalDate.now().plusDays(7));
        rental.setStatus(RentalStatus.ACTIVE);
        rentalRepo.insert(rental);

        Rental fromDb = rentalRepo.findById(reader.getId(), rental.getId());
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals(RentalStatus.ACTIVE, fromDb.getStatus());

        rental.setStatus(RentalStatus.RETURNED);
        rental.setReturnDate(LocalDate.now());
        rentalRepo.update(rental);

        Rental updated = rentalRepo.findById(reader.getId(), rental.getId());
        Assertions.assertEquals(RentalStatus.RETURNED, updated.getStatus());

        rentalRepo.delete(reader.getId(), rental.getId());
        Assertions.assertNull(rentalRepo.findById(reader.getId(), rental.getId()));
    }
}