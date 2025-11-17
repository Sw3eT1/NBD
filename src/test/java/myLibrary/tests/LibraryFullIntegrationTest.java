package myLibrary.tests;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import myLibrary.enums.BookGenre;
import myLibrary.enums.BookStatus;
import myLibrary.enums.RentalStatus;
import myLibrary.models.*;
import myLibrary.repositories.*;
import myLibrary.services.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

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

        ConnectionString connectionString = new ConnectionString(
                "mongodb://admin:adminpassword@mongo1:27017,mongo2:27017,mongo3:27017/?authSource=admin&replicaSet=rs0"
        );

        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(
                        PojoCodecProvider.builder()
                                .register(BookGenre.class)
                                .register(BookStatus.class)
                                .register(RentalStatus.class)
                                .register(ReaderType.class)     // jeżeli masz enum lub klasę abstrakcyjną
                                .automatic(true)
                                .build()
                )
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(pojoCodecRegistry)
                .build();

        client = MongoClients.create(settings);

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
        LibraryRepository libraryRepo = new LibraryRepository(client,db);
        BookRepository bookRepo = new BookRepository(client,db);
        BookCopyRepository copyRepo = new BookCopyRepository(client,db);
        EmployeeRepository employeeRepo = new EmployeeRepository(client,db);
        ReaderRepository readerRepo = new ReaderRepository(client, db);
        RentalRepository rentalRepo = new RentalRepository(client, db);
        ReaderTypeRepository readerTypeRepo = new ReaderTypeRepository(client, db);

        // Init services
        libraryService = new LibraryService(libraryRepo);
        bookService = new BookService(bookRepo, copyRepo);
        employeeService = new EmployeeService(employeeRepo);
        readerService = new ReaderService(readerRepo, readerTypeRepo);
        rentalService = new RentalService(rentalRepo, copyRepo, readerRepo);
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

        // --- 1. Przygotowanie adresów ---
        Address libAddress = new Address("1", "Główna", "Warszawa", "Mazowieckie", "00-001", "Polska");
        Address empAddress = new Address("2", "Boczna", "Warszawa", "Mazowieckie", "00-002", "Polska");
        Address readerAddress = new Address("3", "Nowa", "Warszawa", "Mazowieckie", "00-003", "Polska");

        // --- 2. Tworzymy bibliotekę ---
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

        // --- 3. Pracownicy ---
        Employee emp1 = new Employee("Jan", "Kowalski", "jan@biblioteka.pl", "111-222-333",
                empAddress, library, "Bibliotekarz", 4000, LocalDate.of(2022, 1, 1));

        Employee emp2 = new Employee("Anna", "Nowak", "anna@biblioteka.pl", "444-555-666",
                empAddress, library, "Bibliotekarz", 4500, LocalDate.of(2023, 2, 15));

        employeeService.addEmployee(emp1);
        employeeService.addEmployee(emp2);

        List<Employee> employees = employeeService.findAll();
        Assertions.assertEquals(2, employees.size(), "Powinno być dwóch pracowników.");

        // --- 4. Typy czytelników ---
        ReaderType teenager = new ReaderTypeTeenager(); // maxBooks = 5
        ReaderType adult = new ReaderTypeAdult();       // maxBooks = 10

        readerTypeService.addReaderType(teenager);
        readerTypeService.addReaderType(adult);

        List<ReaderType> readerTypes = readerTypeService.findAll();
        Assertions.assertEquals(2, readerTypes.size(), "Powinny być dwa typy czytelników.");

        // --- 5. Czytelnicy ---
        Reader reader1 = new Reader("Michał", "Wiśniewski", "r1@mail.com", "555-123",
                readerAddress, library, "R001", teenager);

        Reader reader2 = new Reader("Katarzyna", "Zielińska", "r2@mail.com", "555-789",
                readerAddress, library, "R002", adult);

        readerService.registerReader(reader1);
        readerService.registerReader(reader2);

        List<Reader> readers = readerService.findAll();
        Assertions.assertEquals(2, readers.size(), "Powinno być dwóch czytelników.");

        // --- 6. Książki ---
        Book book1 = new Book("Pan Tadeusz", "Adam Mickiewicz", "111-111", BookGenre.CLASSIC);
        Book book2 = new Book("W pustyni i w puszczy", "Henryk Sienkiewicz", "222-222", BookGenre.CLASSIC);

        bookService.addBook(book1);
        bookService.addBook(book2);

        Assertions.assertNotNull(bookService.find(book1.getId()), "Book1 powinien istnieć w bazie.");
        Assertions.assertNotNull(bookService.find(book2.getId()), "Book2 powinien istnieć w bazie.");

        // --- 7. Egzemplarze ---
        BookCopy copy1 = bookCopyService.createCopy(book1.getId(), library.getId());
        BookCopy copy2 = bookCopyService.createCopy(book1.getId(), library.getId());
        BookCopy copy3 = bookCopyService.createCopy(book2.getId(), library.getId());

        List<BookCopy> book1Copies = bookService.getCopies(book1.getId());
        Assertions.assertEquals(2, book1Copies.size(), "Książka 1 powinna mieć 2 egzemplarze.");

        // --- 8. Wypożyczenia ---
        rentalService.rent(reader1, copy1, LocalDate.now().plusDays(7));
        rentalService.rent(reader2, copy3, LocalDate.now().plusDays(7));

        List<Rental> activeReader1 = rentalService.findActiveRentalsByReader(reader1.getId());
        List<Rental> activeReader2 = rentalService.findActiveRentalsByReader(reader2.getId());

        Assertions.assertEquals(1, activeReader1.size(), "Reader1 powinien mieć 1 aktywne wypożyczenie.");
        Assertions.assertEquals(1, activeReader2.size(), "Reader2 powinien mieć 1 aktywne wypożyczenie.");

        // --- 9. Próba wypożyczenia już wypożyczonej kopii ---
        Assertions.assertThrows(IllegalStateException.class, () ->
                        rentalService.rent(reader2, copy1, LocalDate.now().plusDays(7)),
                "Wypożyczenie już wypożyczonej kopii powinno rzucać wyjątek."
        );

        // --- 10. Zwrot książki (Reader1 zwraca swój egzemplarz) ---
        Rental rental1 = activeReader1.get(0);
        rentalService.returnBook(rental1.getId());

        Rental returned = rentalService.findById(rental1.getId());
        Assertions.assertEquals(RentalStatus.RETURNED, returned.getStatus(), "Status wypożyczenia powinien być RETURNED.");
        Assertions.assertNotNull(returned.getReturnDate(), "Data zwrotu powinna być ustawiona.");

        // sprawdzamy, że kopia znowu jest dostępna
        List<BookCopy> book1CopiesAfterReturn = bookService.getCopies(book1.getId());
        BookCopy copy1FromDb = book1CopiesAfterReturn.stream()
                .filter(c -> c.getId().equals(copy1.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Egzemplarz copy1 powinien istnieć."));

        Assertions.assertEquals(BookStatus.AVAILABLE, copy1FromDb.getStatus(),
                "Po zwrocie status egzemplarza powinien być AVAILABLE.");

        // --- 11. Test limitu wypożyczeń dla nastolatka (Teenager, max 5) ---
        // Reader1 ma teraz 0 aktywnych wypożyczeń (swój zwrócił).
        // Spróbujemy wypożyczyć 5 książek => sukces, 6-ta powinna się nie udać.

        for (int i = 1; i <= 5; i++) {
            Book b = new Book("Test" + i, "Autor", "TB-" + i, BookGenre.SCIENCE_FICTION);
            bookService.addBook(b);
            BookCopy c = bookCopyService.createCopy(b.getId(), library.getId());
            rentalService.rent(reader1, c, LocalDate.now().plusDays(7));
        }

        List<Rental> activeAfterLimit = rentalService.findActiveRentalsByReader(reader1.getId());
        Assertions.assertEquals(5, activeAfterLimit.size(),
                "Reader1 (Teenager) powinien mieć dokładnie 5 aktywnych wypożyczeń.");

        // Próba 6-tego wypożyczenia
        Book extra = new Book("Extra", "Autor", "TB-EXTRA", BookGenre.SCIENCE_FICTION);
        bookService.addBook(extra);
        BookCopy extraCopy = bookCopyService.createCopy(extra.getId(), library.getId());

        Assertions.assertThrows(IllegalStateException.class, () ->
                        rentalService.rent(reader1, extraCopy, LocalDate.now().plusDays(7)),
                "Próba przekroczenia limitu wypożyczeń powinna rzucić wyjątek."
        );

        // --- 12. Aktualizacja pracownika ---
        emp1.setSalary(5000);
        employeeService.updateEmployee(emp1);

        Employee updatedEmp1 = employeeService.findBySurname("Kowalski").stream()
                .filter(e -> e.getEmail().equals("jan@biblioteka.pl"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Pracownik Jan Kowalski powinien istnieć."));

        Assertions.assertEquals(5000, updatedEmp1.getSalary(), 0.001,
                "Wynagrodzenie pracownika powinno zostać zaktualizowane.");

        // --- 13. Aktualizacja książki ---
        book1.setDescription("Opis testowy");
        bookService.updateBook(book1);

        Book updatedBook1 = bookService.find(book1.getId());
        Assertions.assertEquals("Opis testowy", updatedBook1.getDescription(),
                "Opis książki powinien zostać zaktualizowany.");

        // --- 14. Usunięcie czytelnika 2 ---
        readerService.deleteReader(reader2.getId());

        List<Reader> readersAfterDelete = readerService.findAll();
        Assertions.assertEquals(1, readersAfterDelete.size(),
                "Po usunięciu jednego czytelnika powinien zostać tylko jeden.");

        boolean existsReader2 = readersAfterDelete.stream()
                .anyMatch(r -> r.getId().equals(reader2.getId()));
        Assertions.assertFalse(existsReader2, "Reader2 nie powinien już istnieć w systemie.");
    }


    @Test
    @Order(2)
    void concurrentRentalRaceConditionTest() throws Exception {

        System.out.println("\n===== TEST LIMITU — WYŚCIG DWÓCH WĄTKÓW =====");

        // 1) Biblioteka + adres
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

        // 2) Reader type Kid = maxBooks = 3
        ReaderType kid = new ReaderTypeKid();
        readerTypeService.addReaderType(kid);

        // 3) Czytelnik
        Reader user = new Reader(
                "Bartek", "Testowy",
                "bartek@test.com", "999999999",
                addr, library, "C001", kid
        );
        readerService.registerReader(user);

        // 4) Dodajemy 4 książki i 4 kopie
        Book b1 = new Book("Book 1", "Author X", "c1", BookGenre.SCIENCE_FICTION);
        Book b2 = new Book("Book 2", "Author X", "c2", BookGenre.SCIENCE_FICTION);
        Book b3 = new Book("Book 3", "Author X", "c3", BookGenre.SCIENCE_FICTION);
        Book b4 = new Book("Book 4", "Author X", "c4", BookGenre.SCIENCE_FICTION);

        bookService.addBook(b1);
        bookService.addBook(b2);
        bookService.addBook(b3);
        bookService.addBook(b4);

        BookCopy c1 = bookCopyService.createCopy(b1.getId(), library.getId());
        BookCopy c2 = bookCopyService.createCopy(b2.getId(), library.getId());
        BookCopy c3 = bookCopyService.createCopy(b3.getId(), library.getId());
        BookCopy c4 = bookCopyService.createCopy(b4.getId(), library.getId());

        // 5) Kid ma 2/3 wypożyczenia — zostało 1 miejsce
        rentalService.rent(user, c1, LocalDate.now().plusDays(7));
        rentalService.rent(user, c2, LocalDate.now().plusDays(7));

        // 6) Synchronizacja startu obu wątków
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);

        ExecutorService pool = Executors.newFixedThreadPool(2);

        Callable<String> task1 = () -> {
            latch.await(); // czekaj aż oba wątki będą gotowe
            try {
                rentalService.rent(user, c3, LocalDate.now().plusDays(7));
                return "THREAD-1 SUCCESS";
            } catch (Exception e) {
                return "THREAD-1 FAIL: " + e.getMessage();
            }
        };

        Callable<String> task2 = () -> {
            latch.await();
            try {
                rentalService.rent(user, c4, LocalDate.now().plusDays(7));
                return "THREAD-2 SUCCESS";
            } catch (Exception e) {
                return "THREAD-2 FAIL: " + e.getMessage();
            }
        };

        Future<String> f1 = pool.submit(task1);
        Future<String> f2 = pool.submit(task2);

        // start obu jednocześnie
        latch.countDown();

        String r1 = f1.get();
        String r2 = f2.get();

        pool.shutdown();

        System.out.println("Wynik 1: " + r1);
        System.out.println("Wynik 2: " + r2);

        boolean success1 = r1.contains("SUCCESS");
        boolean success2 = r2.contains("SUCCESS");

        // 7) TYLKO JEDEN może się udać
        Assertions.assertTrue(success1 ^ success2,
                "Dokładnie JEDEN wątek ma wypożyczyć książkę — nigdy oba!");

        // 8) Kid może mieć tylko 3 aktywne wypożyczenia
        List<Rental> active = rentalService.findActiveRentalsByReader(user.getId());

        Assertions.assertEquals(3, active.size(),
                "Czytelnik nie może posiadać więcej niż 3 aktywnych wypożyczeń (Kid).");

        System.out.println("Test przeszedł — limit działa prawidłowo i race condition obsłużone.");
    }

}
