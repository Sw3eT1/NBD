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
        bookDao.create(book);

        // READ
        Book fromDb = bookDao.findById(book.getId());
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals(book.getTitle(), fromDb.getTitle());

        // UPDATE
        book.setDescription("updated");
        bookDao.update(book);

        Book updated = bookDao.findById(book.getId());
        Assertions.assertEquals("updated", updated.getDescription());

        // DELETE
        bookDao.delete(book);
        Book deleted = bookDao.findById(book.getId());
        Assertions.assertNull(deleted);
    }

    @Test
    @Order(2)
    void libraryRepositoryCrud() {
        Address addr = new Address("1", "Main", "City", "State", "00-000", "Country");
        Library library = new Library("Lib", addr, "111", "lib@mail.com", "site", true, "8-16");

        libraryDao.create(library);
        Library fromDb = libraryDao.findById(library.getId());
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals("Lib", fromDb.getName());

        library.setOpeningHours("9-17");
        libraryDao.update(library);
        Library updated = libraryDao.findById(library.getId());
        Assertions.assertEquals("9-17", updated.getOpeningHours());

        libraryDao.delete(library);
        Assertions.assertNull(libraryDao.findById(library.getId()));
    }

    @Test
    @Order(3)
    void readerTypeRepositoryCrud() {
        ReaderTypeAdult adult = new ReaderTypeAdult();

        readerTypeDao.create(adult);
        ReaderType fromDb = readerTypeDao.findById(adult.getId());
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals("ADULT", fromDb.getName());

        fromDb.setMaxBooks(20);
        readerTypeDao.update(fromDb);

        ReaderType updated = readerTypeDao.findById(adult.getId());
        Assertions.assertEquals(20, updated.getMaxBooks());

        readerTypeDao.delete(adult);
        Assertions.assertNull(readerTypeDao.findById(adult.getId()));
    }

    @Test
    @Order(4)
    void readerRepositoryCrud() {
        Address addr = new Address("2", "Street", "City", "State", "00-001", "Country");
        Library library = new Library("Lib2", addr, "222", "l2@mail.com", "site2", false, "10-18");
        libraryDao.create(library);

        ReaderTypeTeenager teenager = new ReaderTypeTeenager();
        readerTypeDao.create(teenager);

        Reader reader = new Reader("Jan", "Kowalski", "jan@mail.com", "123",
                addr, library, "CARD-1", teenager);

        readerDao.create(reader);

        Reader fromDb = readerDao.findById(library.getId(), reader.getId());
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals("Jan", fromDb.getName());

        reader.setActiveRentals(3);
        readerDao.update(reader);

        Reader updated = readerDao.findById(library.getId(), reader.getId());
        Assertions.assertEquals(3, updated.getActiveRentals());

        readerDao.delete(reader);
        Assertions.assertNull(readerDao.findById(library.getId(), reader.getId()));
    }

    @Test
    @Order(5)
    void employeeRepositoryCrud() {
        Address addr = new Address("3", "Street", "City", "State", "00-002", "Country");
        Library library = new Library("Lib3", addr, "333", "l3@mail.com", "site3", true, "9-19");
        libraryDao.create(library);

        Employee emp = new Employee("Ewa", "Nowak", "ewa@mail.com", "555",
                addr, library, "Librarian", 3000, LocalDate.of(2020, 1, 1));

        employeeDao.create(emp);

        Employee fromDb = employeeDao.findById(library.getId(), emp.getId());
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals("Ewa", fromDb.getName());

        emp.setSalary(3500);
        employeeDao.update(emp);
        Employee updated = employeeDao.findById(library.getId(), emp.getId());
        Assertions.assertEquals(3500, updated.getSalary());

        employeeDao.delete(emp);
        Assertions.assertNull(employeeDao.findById(library.getId(), emp.getId()));
    }

    @Test
    @Order(6)
    void bookCopyRepositoryCrud() {
        Address addr = new Address("4", "Street", "City", "State", "00-003", "Country");
        Library library = new Library("Lib4", addr, "444", "l4@mail.com", "site4", true, "8-18");
        libraryDao.create(library);

        Book book = new Book("CopyBook", "Author", "COPY-1", BookGenre.SCIENCE_FICTION);
        bookDao.create(book);

        BookCopy copy = new BookCopy(book, library);
        copy.setStatusEnum(BookStatus.AVAILABLE);
        bookCopyDao.create(copy);

        BookCopy fromDb = bookCopyDao.findById(library.getId(), book.getId(), copy.getId());
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals(BookStatus.AVAILABLE, fromDb.getStatusEnum());

        copy.setStatusEnum(BookStatus.RENTED);
        bookCopyDao.update(copy);
        BookCopy updated = bookCopyDao.findById(library.getId(), book.getId(), copy.getId());
        Assertions.assertEquals(BookStatus.RENTED, updated.getStatusEnum());

        bookCopyDao.delete(copy);
        Assertions.assertNull(bookCopyDao.findById(library.getId(), book.getId(), copy.getId()));
    }

    @Test
    @Order(7)
    void rentalRepositoryCrud() {
        Address addr = new Address("5", "Street", "City", "State", "00-004", "Country");
        Library library = new Library("Lib5", addr, "555", "l5@mail.com", "site5", true, "7-17");
        libraryDao.create(library);

        ReaderTypeKid kid = new ReaderTypeKid();
        readerTypeDao.create(kid);

        Reader reader = new Reader("Ola", "Test", "ola@mail.com", "777",
                addr, library, "CARD-2", kid);
        readerDao.create(reader);

        Book book = new Book("RentalBook", "Author", "REN-1", BookGenre.SCIENCE_FICTION);
        bookDao.create(book);

        BookCopy copy = new BookCopy(book, library);
        copy.setStatusEnum(BookStatus.AVAILABLE);
        bookCopyDao.create(copy);

        Rental rental = new Rental(reader, copy, LocalDate.now(), LocalDate.now().plusDays(7));
        rental.setStatusEnum(RentalStatus.ACTIVE);
        rentalDao.create(rental);

        Rental fromDb = rentalDao.findById(reader.getId(), rental.getId());
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals(RentalStatus.ACTIVE, fromDb.getStatusEnum());

        rental.setStatusEnum(RentalStatus.RETURNED);
        rental.setReturnDate(LocalDate.now());
        rentalDao.update(rental);

        Rental updated = rentalDao.findById(reader.getId(), rental.getId());
        Assertions.assertEquals(RentalStatus.RETURNED, updated.getStatusEnum());

        rentalDao.delete(rental);
        Assertions.assertNull(rentalDao.findById(reader.getId(), rental.getId()));
    }
}