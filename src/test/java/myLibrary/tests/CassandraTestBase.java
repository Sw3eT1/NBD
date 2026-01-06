package myLibrary.tests;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import myLibrary.config.CassandraConnector;
import myLibrary.repositories.*;
import myLibrary.services.*;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class CassandraTestBase {

    protected CqlSession session;

    // DAO
    protected LibraryDao libraryDao;
    protected BookDao bookDao;
    protected BookCopyDao bookCopyDao;
    protected EmployeeDao employeeDao;
    protected ReaderDao readerDao;
    protected ReaderTypeDao readerTypeDao;
    protected RentalDao rentalDao;

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
        // 1. Sesja
        session = CassandraConnector.getSession();

        // 2. Wyczyść tabele (żeby testy startowały na pustej bazie)
        session.execute("TRUNCATE library.libraries_by_id");
        session.execute("TRUNCATE library.books_by_id");
        session.execute("TRUNCATE library.book_copies_by_library");
        session.execute("TRUNCATE library.employees_by_library");
        session.execute("TRUNCATE library.readers_by_library");
        session.execute("TRUNCATE library.reader_types");
        session.execute("TRUNCATE library.rentals_by_reader");

        // 3. Zbuduj Mapper i DAO
        LibraryMapper mapper = new LibraryMapperBuilder(session).build();
        CqlIdentifier ks = CqlIdentifier.fromCql("library");

        libraryDao     = mapper.libraryDao(ks);
        bookDao        = mapper.bookDao(ks);
        bookCopyDao    = mapper.bookCopyDao(ks);
        employeeDao    = mapper.employeeDao(ks);
        readerDao      = mapper.readerDao(ks);
        readerTypeDao  = mapper.readerTypeDao(ks);
        rentalDao      = mapper.rentalDao(ks);

        // 4. Zbuduj serwisy na DAO
        libraryService     = new LibraryService(libraryDao);
        bookService        = new BookService(bookDao);
        bookCopyService    = new BookCopyService(bookDao, libraryDao, bookCopyDao);
        employeeService    = new EmployeeService(employeeDao);
        readerTypeService  = new ReaderTypeService(readerTypeDao);
        readerService      = new ReaderService(readerDao, readerTypeDao);
        rentalService      = new RentalService(rentalDao, bookCopyDao, readerDao);
    }

    @AfterAll
    void tearDownCassandra() {
        CassandraConnector.close();
    }
}