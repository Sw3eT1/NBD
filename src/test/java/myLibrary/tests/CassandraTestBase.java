package myLibrary.tests;

import com.datastax.oss.driver.api.core.CqlSession;
import myLibrary.config.CassandraConnector;
import myLibrary.repositories.*;
import myLibrary.services.*;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class CassandraTestBase {

    protected CqlSession session;

    // Repos
    protected LibraryRepository libraryRepo;
    protected BookRepository bookRepo;
    protected BookCopyRepository copyRepo;
    protected EmployeeRepository employeeRepo;
    protected ReaderRepository readerRepo;
    protected ReaderTypeRepository readerTypeRepo;
    protected RentalRepository rentalRepo;

    // Services
    protected LibraryService libraryService;
    protected BookService bookService;
    protected BookCopyService bookCopyService;
    protected EmployeeService employeeService;
    protected ReaderService readerService;
    protected ReaderTypeService readerTypeService;
    protected RentalService rentalService;

    @BeforeAll
    void setupCassandra() {
        session = CassandraConnector.getSession();

        // Czyścimy tabele – zakładam, że wszystkie istnieją
        session.execute("TRUNCATE library.libraries_by_id");
        session.execute("TRUNCATE library.books_by_id");
        session.execute("TRUNCATE library.book_copies_by_library");
        session.execute("TRUNCATE library.employees_by_library");
        session.execute("TRUNCATE library.readers_by_library");
        session.execute("TRUNCATE library.reader_types");
        session.execute("TRUNCATE library.rentals_by_reader");

        // Repos
        libraryRepo = new LibraryRepository(session);
        bookRepo = new BookRepository(session);
        copyRepo = new BookCopyRepository(session);
        employeeRepo = new EmployeeRepository(session);
        readerRepo = new ReaderRepository(session);
        readerTypeRepo = new ReaderTypeRepository(session);
        rentalRepo = new RentalRepository(session);

        // Services
        libraryService = new LibraryService(libraryRepo);
        bookService = new BookService(bookRepo);
        bookCopyService = new BookCopyService(bookRepo, libraryRepo, copyRepo);
        employeeService = new EmployeeService(employeeRepo);
        readerTypeService = new ReaderTypeService(readerTypeRepo);
        readerService = new ReaderService(readerRepo, readerTypeRepo);
        rentalService = new RentalService(rentalRepo, copyRepo, readerRepo);
    }

    @AfterAll
    void tearDownCassandra() {
        CassandraConnector.close();
    }
}