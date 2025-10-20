package myLibrary;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import myLibrary.enums.BookGenre;

import myLibrary.managers.*;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("LibraryUnit");
        EntityManager em = emf.createEntityManager();

        // --- MANAGERS ---
        LibraryManager libraryManager = new LibraryManager(em);
        BookManager bookManager = new BookManager(em);
        EmployeeManager employeeManager = new EmployeeManager(em);
        ReaderManager readerManager = new ReaderManager(em);
        RentalManager rentalManager = new RentalManager(em);

        // --- CREATE ADDRESSES ---
        Address libAddress = new Address("1", "Główna", "Warszawa", "Mazowieckie", "00-001", "Polska");
        Address empAddress = new Address("2", "Boczna", "Warszawa", "Mazowieckie", "00-002", "Polska");
        Address readerAddress = new Address("3", "Nowa", "Warszawa", "Mazowieckie", "00-003", "Polska");

        // --- CREATE LIBRARY ---
        Library library = new Library(
                "Biblioteka Centralna",
                libAddress,
                "123-456-789",
                "kontakt@biblioteka.pl",
                "www.biblioteka.pl",
                true,
                "08:00-20:00"
        );
        libraryManager.addLibrary(library);

        // --- CREATE EMPLOYEES ---
        Employee emp1 = new Employee("Jan", "Kowalski", "jan@biblioteka.pl", "111-222-333", empAddress,
                library, "Bibliotekarz", 4000, LocalDate.of(2022,1,1));
        Employee emp2 = new Employee("Anna", "Nowak", "anna@biblioteka.pl", "444-555-666", empAddress,
                library, "Bibliotekarz", 4500, LocalDate.of(2023,2,15));
        employeeManager.addEmployee(emp1, library);
        employeeManager.addEmployee(emp2, library);

        // --- CREATE READER TYPES ---
        ReaderType teenager = new ReaderTypeTeenager();
        ReaderType adultType = new ReaderTypeAdult();
        readerManager.addReaderType(teenager);
        readerManager.addReaderType(adultType);

        // --- REGISTER READERS ---
        Reader reader1 = new Reader("Michał", "Wiśniewski", "reader1@email.com", "555-123-456",
                readerAddress, library, "R001", teenager);
        Reader reader2 = new Reader("Katarzyna", "Zielińska", "reader2@email.com", "555-789-012",
                readerAddress, library, "R002", adultType);
        readerManager.registerReader(reader1);
        readerManager.registerReader(reader2);

        // --- CREATE BOOKS ---
        Book book1 = new Book("Pan Tadeusz", "Adam Mickiewicz", "111-111", BookGenre.CLASSIC);
        Book book2 = new Book("W pustyni i w puszczy", "Henryk Sienkiewicz", "222-222", BookGenre.CLASSIC);
        bookManager.addBook(book1);
        bookManager.addBook(book2);

        // --- CREATE BOOK COPIES (linked to library) ---
        BookCopy copy1 = new BookCopy(book1, library);
        BookCopy copy2 = new BookCopy(book1, library);
        BookCopy copy3 = new BookCopy(book2, library);
        bookManager.addBookCopy(book1, copy1);
        bookManager.addBookCopy(book1, copy2);
        bookManager.addBookCopy(book2, copy3);

        // --- RENTAL OPERATIONS ---
        System.out.println("\n--- WYPOŻYCZENIA ---");
        rentalManager.rentBook(reader1, copy1, LocalDate.now().plusDays(14));
        rentalManager.rentBook(reader2, copy3, LocalDate.now().plusDays(7));

        // --- ATTEMPT TO RENT ALREADY RENTED BOOK ---
        try {
            rentalManager.rentBook(reader2, copy1, LocalDate.now().plusDays(7));
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }

        // --- RETURN BOOK ---
        rentalManager.returnBook(rentalManager.findActiveRentals().getFirst().getId());

        System.out.println(copy1.getStatus());


        // --- LIST ACTIVE RENTALS ---
        System.out.println("\n--- AKTYWNE WYPOŻYCZENIA ---");
        for (Rental r : rentalManager.findActiveRentals()) {
            System.out.println(r.getReader().getName() + " wypożyczył " + r.getBookCopy().getBook().getTitle());
        }

        em.close();
        emf.close();
    }
}
