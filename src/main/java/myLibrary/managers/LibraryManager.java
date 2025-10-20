package myLibrary.managers;

import jakarta.persistence.EntityManager;
import myLibrary.Library;
import myLibrary.BookCopy;
import myLibrary.Employee;
import myLibrary.Reader;
import myLibrary.repositories.LibraryRespository;

import java.util.UUID;

public class LibraryManager extends BaseManager {
    private final LibraryRespository libraryRepo;

    public LibraryManager(EntityManager em) {
        super(em);
        this.libraryRepo = new LibraryRespository(em);
    }

    public void addLibrary(Library library) {
        executeInTransaction(() -> libraryRepo.add(library));
    }

    public void addBookCopy(Library library, BookCopy copy) {
        executeInTransaction(() -> {
            if (copy.getId() == null) em.persist(copy);
            library.addBookCopy(copy);
            libraryRepo.update(library);
        });
    }

    public void addEmployee(Library library, Employee employee) {
        executeInTransaction(() -> {
            if (employee.getId() == null) em.persist(employee);
            library.addEmployee(employee);
            libraryRepo.update(library);
        });
    }

    public void addMember(Library library, Reader reader) {
        executeInTransaction(() -> {
            if (reader.getId() == null) em.persist(reader);
            library.addMember(reader);
            libraryRepo.update(library);
        });
    }

    public Library findLibrary(UUID id) {
        return libraryRepo.find(id);
    }

    public void removeLibrary(Library library) {
        executeInTransaction(() -> libraryRepo.delete(library));
    }

    public void updateLibrary(Library library) {
        executeInTransaction(() -> libraryRepo.update(library));
    }
}

