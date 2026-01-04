package myLibrary.tests;

import myLibrary.enums.BookGenre;
import myLibrary.enums.BookStatus;
import myLibrary.enums.RentalStatus;
import myLibrary.models.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServicesIntegrationTest extends CassandraTestBase {

    @Test
    @Order(1)
    void libraryServiceCrudThroughService() {
        Address addr = new Address("10", "Serwisowa", "Kraków", "Małopolskie", "30-000", "Polska");
        Library library = new Library("Service Library", addr, "100-200-300",
                "service@lib.pl", "servicelib.pl", true, "09-17");

        // CREATE
        libraryService.addLibrary(library);

        // READ
        Library fromDb = libraryService.find(library.getId());
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals("Service Library", fromDb.getName());

        // UPDATE
        library.setOpeningHours("10-18");
        libraryService.update(library);

        Library updated = libraryService.find(library.getId());
        Assertions.assertEquals("10-18", updated.getOpeningHours());

        // DELETE (na osobnym obiekcie, żeby główna biblioteka została na kolejne testy)
        Library toDelete = new Library("ToDelete", addr, "111", "del@lib.pl", "del.pl",
                false, "08-16");
        libraryService.addLibrary(toDelete);
        libraryService.delete(toDelete.getId());
        Assertions.assertNull(libraryService.find(toDelete.getId()));
    }

    @Test
    @Order(2)
    void readerTypeAndReaderServicesCrud() {
        // Potrzebujemy biblioteki dla czytelników
        Address addr = new Address("20", "Czytelnicza", "Poznań", "Wielkopolskie", "60-000", "Polska");
        Library library = new Library("ReaderLib", addr, "200-300-400",
                "reader@lib.pl", "readerlib.pl", false, "10-19");
        libraryService.addLibrary(library);

        // ReaderTypeService – CREATE + READ
        ReaderTypeAdult adult = new ReaderTypeAdult();
        readerTypeService.addReaderType(adult);

        ReaderType fromDb = readerTypeService.getReaderType(adult.getId());
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals("ADULT", fromDb.getName());

        // UPDATE
        fromDb.setMaxBooks(15);
        readerTypeService.updateReaderType(fromDb);

        ReaderType updatedType = readerTypeService.getReaderType(adult.getId());
        Assertions.assertEquals(15, updatedType.getMaxBooks());

        // ReaderService – CREATE + READ + UPDATE
        Reader reader = new Reader(
                "Jan",
                "Nowak",
                "jan.nowak@example.com",
                "500-600-700",
                addr,
                library,
                "CARD-100",
                adult
        );

        readerService.registerReader(reader);

        Reader readerFromDb = readerService.getReader(library.getId(), reader.getId());
        Assertions.assertNotNull(readerFromDb);
        Assertions.assertEquals("Jan", readerFromDb.getName());

        // UPDATE (np. zmiana liczby aktywnych wypożyczeń)
        readerFromDb.setActiveRentals(2);
        readerService.updateReader(readerFromDb);

        Reader updatedReader = readerService.getReader(library.getId(), reader.getId());
        Assertions.assertEquals(2, updatedReader.getActiveRentals());

        // DELETE readerType na osobnym typie, żeby adult został do dalszych testów
        ReaderTypeKid kid = new ReaderTypeKid();
        readerTypeService.addReaderType(kid);
        readerTypeService.deleteReaderType(kid.getId());
        Assertions.assertNull(readerTypeService.getReaderType(kid.getId()));
    }

    @Test
    @Order(3)
    void bookAndBookCopyServicesWorkflow() {
        // Biblioteka
        Address addr = new Address("30", "Książkowa", "Gdańsk", "Pomorskie", "80-000", "Polska");
        Library library = new Library("BookLib", addr, "300-400-500",
                "book@lib.pl", "booklib.pl", true, "08-18");
        libraryService.addLibrary(library);

        // BookService – CREATE + READ + UPDATE + DELETE
        Book book = new Book("Service Book", "Autor A", "ISBN-S-1", BookGenre.FANTASY);
        bookService.addBook(book);

        Book bookFromDb = bookService.find(book.getId());
        Assertions.assertNotNull(bookFromDb);
        Assertions.assertEquals("Service Book", bookFromDb.getTitle());

        book.setDescription("Opis książki testowej");
        bookService.updateBook(book);
        Book updatedBook = bookService.find(book.getId());
        Assertions.assertEquals("Opis książki testowej", updatedBook.getDescription());

        // BookCopyService – utworzenie kopii i zmiana statusu
        BookCopy copy = bookCopyService.createCopy(book.getId(), library.getId());
        Assertions.assertNotNull(copy);

        // Domyślny status po utworzeniu
        BookCopy copyFromDb = copyRepo.findById(library.getId(), book.getId(), copy.getId());
        Assertions.assertNotNull(copyFromDb);
        Assertions.assertEquals(BookStatus.AVAILABLE, copyFromDb.getStatus());

        // Zmiana statusu na RENTED przez serwis
        bookCopyService.changeStatus(library.getId(), book.getId(), copy.getId(), BookStatus.RENTED);

        BookCopy copyAfterChange = copyRepo.findById(library.getId(), book.getId(), copy.getId());
        Assertions.assertEquals(BookStatus.RENTED, copyAfterChange.getStatus());

        // DELETE książki (kopii nie ruszamy, bo może być jeszcze użyta w innych scenariuszach)
        bookService.deleteBook(book.getId());
        Assertions.assertNull(bookService.find(book.getId()));
    }

    @Test
    @Order(4)
    void employeeServiceCrud() {
        Address addr = new Address("40", "Pracownicza", "Łódź", "Łódzkie", "90-000", "Polska");
        Library library = new Library("EmpLib", addr, "400-500-600",
                "emp@lib.pl", "emplib.pl", true, "08-16");
        libraryService.addLibrary(library);

        // CREATE
        Employee emp = new Employee(
                "Ewa",
                "Kowalska",
                "ewa.kowalska@example.com",
                "600-700-800",
                addr,
                library,
                "Bibliotekarz",
                3500,
                LocalDate.of(2021, 5, 10)
        );

        employeeService.addEmployee(emp);

        // READ
        Employee fromDb = employeeService.getEmployee(library.getId(), emp.getId());
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals("Ewa", fromDb.getName());

        // UPDATE
        emp.setSalary(4000);
        employeeService.updateEmployee(emp);

        Employee updated = employeeService.getEmployee(library.getId(), emp.getId());
        Assertions.assertEquals(4000, updated.getSalary(), 0.001);

        // DELETE
        employeeService.deleteEmployee(library.getId(), emp.getId());
        Assertions.assertNull(employeeService.getEmployee(library.getId(), emp.getId()));
    }

    @Test
    @Order(5)
    void rentalServiceWorkflow() {
        // 1. Biblioteka
        Address addr = new Address("50", "Wypożyczeniowa", "Rzeszów", "Podkarpackie", "35-000", "Polska");
        Library library = new Library("RentalLib", addr, "500-600-700",
                "rental@lib.pl", "rentallib.pl", true, "09-19");
        libraryService.addLibrary(library);

        // 2. Typ czytelnika
        ReaderTypeTeenager teen = new ReaderTypeTeenager();
        readerTypeService.addReaderType(teen);

        // 3. Czytelnik
        Reader reader = new Reader(
                "Marek",
                "Czytelnik",
                "marek@example.com",
                "700-800-900",
                addr,
                library,
                "CARD-200",
                teen
        );
        readerService.registerReader(reader);

        // 4. Książka + kopia
        Book book = new Book("Rental Service Book", "Autor B", "ISBN-R-1", BookGenre.SCIENCE_FICTION);
        bookService.addBook(book);

        BookCopy copy = bookCopyService.createCopy(book.getId(), library.getId());
        BookCopy copyFromDb = copyRepo.findById(library.getId(), book.getId(), copy.getId());
        Assertions.assertEquals(BookStatus.AVAILABLE, copyFromDb.getStatus());

        // 5. Wypożyczenie przez RentalService
        Rental rental = rentalService.rent(reader, copy, LocalDate.now().plusDays(14));

        Rental rentalFromDb = rentalService.findById(reader.getId(), rental.getId());
        Assertions.assertNotNull(rentalFromDb);
        Assertions.assertEquals(RentalStatus.ACTIVE, rentalFromDb.getStatus());

        // Sprawdzamy, że licznik aktywnych wypożyczeń czytelnika wzrósł
        Reader readerAfterRent = readerService.getReader(library.getId(), reader.getId());
        Assertions.assertEquals(1, readerAfterRent.getActiveRentals());

        // Sprawdzamy, że kopia ma status RENTED
        BookCopy copyAfterRent = copyRepo.findById(library.getId(), book.getId(), copy.getId());
        Assertions.assertEquals(BookStatus.RENTED, copyAfterRent.getStatus());

        // 6. Zwrot książki
        rentalService.returnBook(
                reader.getId(),
                rental.getId(),
                library.getId(),
                book.getId(),
                copy.getId()
        );

        Rental rentalAfterReturn = rentalService.findById(reader.getId(), rental.getId());
        Assertions.assertEquals(RentalStatus.RETURNED, rentalAfterReturn.getStatus());
        Assertions.assertNotNull(rentalAfterReturn.getReturnDate());

        // Kopia jest znowu AVAILABLE
        BookCopy copyAfterReturn = copyRepo.findById(library.getId(), book.getId(), copy.getId());
        Assertions.assertEquals(BookStatus.AVAILABLE, copyAfterReturn.getStatus());

        // Licznik aktywnych wypożyczeń powinien wrócić do 0 (nasza logika biznesowa)
        Reader readerAfterReturn = readerService.getReader(library.getId(), reader.getId());
        Assertions.assertEquals(0, readerAfterReturn.getActiveRentals());
    }
}