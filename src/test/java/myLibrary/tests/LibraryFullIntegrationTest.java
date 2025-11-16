package myLibrary.tests;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import myLibrary.enums.BookGenre;
import myLibrary.models.*;
import myLibrary.repositories.*;
import myLibrary.services.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LibraryFullIntegrationTest {

    private MongoClient client;
    private MongoDatabase db;

    private LibraryService libraryService;
    private BookService bookService;
    private BookCopyService bookCopyService;
    private EmployeeService employeeService;
    private ReaderService readerService;
    private ReaderTypeService readerTypeService;
    private RentalService rentalService;

    @BeforeAll
    void setup() {

        client = MongoClients.create(
                "mongodb://admin:adminpassword@localhost:27017/?authSource=admin&replicaSet=rs0"
        );

        db = client.getDatabase("library_test_db");

        // Clean DB
        db.getCollection("libraries").drop();
        db.getCollection("books").drop();
        db.getCollection("bookCopies").drop();
        db.getCollection("employees").drop();
        db.getCollection("readers").drop();
        db.getCollection("rentals").drop();
        db.getCollection("readerTypes").drop();

        // Init repositories
        LibraryRepository libraryRepo = new LibraryRepository(db);
        BookRepository bookRepo = new BookRepository(db);
        BookCopyRepository copyRepo = new BookCopyRepository(db);
        EmployeeRepository employeeRepo = new EmployeeRepository(db);
        ReaderRepository readerRepo = new ReaderRepository(db);
        RentalRepository rentalRepo = new RentalRepository(db);
        ReaderTypeRepository readerTypeRepo = new ReaderTypeRepository(db);

        // Init services
        libraryService = new LibraryService(libraryRepo);
        bookService = new BookService(bookRepo, copyRepo);
        employeeService = new EmployeeService(employeeRepo);
        readerService = new ReaderService(readerRepo, readerTypeRepo);
        rentalService = new RentalService(rentalRepo, copyRepo);
        readerTypeService = new ReaderTypeService(readerTypeRepo);
        bookCopyService = new BookCopyService(bookRepo, libraryRepo, copyRepo);
    }

    @AfterAll
    void teardown() {
        client.close();
    }

    @Test
    @Order(1)
    void fullLibraryWorkflowTest() {

        // CREATE ADDRESSES
        Address libAddress = new Address("1", "Główna", "Warszawa", "Mazowieckie", "00-001", "Polska");
        Address empAddress = new Address("2", "Boczna", "Warszawa", "Mazowieckie", "00-002", "Polska");
        Address readerAddress = new Address("3", "Nowa", "Warszawa", "Mazowieckie", "00-003", "Polska");

        // CREATE LIBRARY
        Library library = new Library(
                "Biblioteka Centralna",
                libAddress,
                "123-456-789",
                "kontakt@biblioteka.pl",
                "www.biblioteka.pl",
                true,
                "08:00-20:00"
        );
        libraryService.addLibrary(library);

        // CREATE EMPLOYEES
        Employee emp1 = new Employee("Jan", "Kowalski", "jan@biblioteka.pl", "111-222-333",
                empAddress, library, "Bibliotekarz", 4000, LocalDate.of(2022, 1, 1));

        Employee emp2 = new Employee("Anna", "Nowak", "anna@biblioteka.pl", "444-555-666",
                empAddress, library, "Bibliotekarz", 4500, LocalDate.of(2023, 2, 15));

        employeeService.addEmployee(emp1);
        employeeService.addEmployee(emp2);

        // CREATE READER TYPES
        ReaderType teenager = new ReaderTypeTeenager();
        ReaderType adult = new ReaderTypeAdult();

        readerTypeService.addReaderType(teenager);
        readerTypeService.addReaderType(adult);

        // REGISTER READERS
        Reader reader1 = new Reader("Michał", "Wiśniewski", "r1@mail.com", "555-123",
                readerAddress, library, "R001", teenager);

        Reader reader2 = new Reader("Katarzyna", "Zielińska", "r2@mail.com", "555-789",
                readerAddress, library, "R002", adult);

        readerService.registerReader(reader1);
        readerService.registerReader(reader2);

        // CREATE BOOKS
        Book book1 = new Book("Pan Tadeusz", "Adam Mickiewicz", "111-111", BookGenre.CLASSIC);
        Book book2 = new Book("W pustyni i w puszczy", "Henryk Sienkiewicz", "222-222", BookGenre.CLASSIC);

        bookService.addBook(book1);
        bookService.addBook(book2);

        // CREATE COPIES
        BookCopy copy1 = bookCopyService.createCopy(book1.getId(), library.getId());
        BookCopy copy2 = bookCopyService.createCopy(book1.getId(), library.getId());
        BookCopy copy3 = bookCopyService.createCopy(book2.getId(), library.getId());

        // RENT
        rentalService.rent(reader1, copy1, LocalDate.now().plusDays(7));
        rentalService.rent(reader2, copy3, LocalDate.now().plusDays(7));

        // RENT ALREADY RENTED
        try {
            rentalService.rent(reader2, copy1, LocalDate.now().plusDays(7));
        } catch (Exception ignored) {}

        // RETURN BOOK
        List<Rental> active = rentalService.findActiveRentals();
        Rental rental = active.getFirst();

        rentalService.returnBook(rental.getId());

        // LIMIT TEST
        try {
            for (int i = 1; i <= 6; i++) {
                Book b = new Book("Test" + i, "Autor", "TB" + i, BookGenre.SCIENCE_FICTION);
                bookService.addBook(b);
                BookCopy c = bookCopyService.createCopy(b.getId(), library.getId());
                rentalService.rent(reader1, c, LocalDate.now().plusDays(7));
            }
        } catch (Exception ignored) {}

        // UPDATE EMPLOYEE
        emp1.setSalary(5000);
        employeeService.updateEmployee(emp1);

        // UPDATE BOOK
        book1.setDescription("Opis testowy");
        bookService.updateBook(book1);

        // DELETE READER
        readerService.deleteReader(reader2.getId());

        // LIST ALL READERS
        for (Reader r : readerService.findAll()) {
            System.out.println(r.getName());
        }
    }

    @Test
    @Order(2)
    void concurrentRentalRaceConditionTest() throws Exception {

        System.out.println("\n===== TEST WYŚCIGU DWÓCH WĄTKÓW =====");

        // 1) Tworzymy bibliotekę i czytelnika z limitem = 1 (Teenager)
        Address addr = new Address("10", "Dworcowa", "Kraków", "Małopolskie", "30-001", "Polska");
        Library library = new Library(
                "Filia 1",
                addr,
                "000-111-222",
                "filia@example.com",
                "filia.pl",
                true,
                "08:00-20:00"
        );
        libraryService.addLibrary(library);

        ReaderType teenager = new ReaderTypeTeenager(); // MAX = 1
        readerTypeService.addReaderType(teenager);

        Reader user = new Reader(
                "Bartek", "Testowy",
                "bartek@test.com", "999999999",
                addr, library, "C001", teenager
        );
        readerService.registerReader(user);

        // 2) Dodajemy 2 książki z 2 egzemplarzami
        Book b1 = new Book("Concurrency Book 1", "Author A", "c1", BookGenre.SCIENCE_FICTION);
        Book b2 = new Book("Concurrency Book 2", "Author A", "c2", BookGenre.SCIENCE_FICTION);

        bookService.addBook(b1);
        bookService.addBook(b2);

        BookCopy copy1 = bookCopyService.createCopy(b1.getId(), library.getId());
        BookCopy copy2 = bookCopyService.createCopy(b2.getId(), library.getId());

        ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(2);

        Callable<String> task1 = () -> {
            try {
                rentalService.rent(user, copy1, LocalDate.now().plusDays(7));
                return "THREAD-1 SUCCESS";
            } catch (Exception e) {
                return "THREAD-1 FAIL: " + e.getMessage();
            }
        };

        Callable<String> task2 = () -> {
            try {
                rentalService.rent(user, copy2, LocalDate.now().plusDays(7));
                return "THREAD-2 SUCCESS";
            } catch (Exception e) {
                return "THREAD-2 FAIL: " + e.getMessage();
            }
        };

        Future<String> f1 = pool.submit(task1);
        Future<String> f2 = pool.submit(task2);

        String r1 = f1.get();
        String r2 = f2.get();

        pool.shutdown();

        System.out.println("Wynik 1: " + r1);
        System.out.println("Wynik 2: " + r2);

        boolean success1 = r1.contains("SUCCESS");
        boolean success2 = r2.contains("SUCCESS");

        Assertions.assertNotEquals(success1, success2,
                "Tylko 1 wątek powinien wypożyczyć książkę! Wyścig nie został poprawnie obsłużony.");

        List<Rental> active = rentalService.findActiveRentals();
        Assertions.assertEquals(1, active.size(),
                "Powinno istnieć dokładnie jedno aktywne wypożyczenie!");

        System.out.println("Test wyścigu przeszedł poprawnie — system nie dopuszcza podwójnego wypożyczenia.");
    }

}
