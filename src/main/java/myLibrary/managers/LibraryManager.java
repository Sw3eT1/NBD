package myLibrary.managers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import myLibrary.Library;
import myLibrary.BookCopy;
import myLibrary.Employee;
import myLibrary.Reader;
import myLibrary.repositories.LibraryRespository;

import java.util.UUID;

public class LibraryManager {

    private final EntityManager em;
    private final LibraryRespository libraryRepo;

    public LibraryManager(EntityManager em) {
        this.em = em;
        this.libraryRepo = new LibraryRespository(em);
    }

    public void addLibrary(Library library) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            libraryRepo.add(library);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void addBookCopy(Library library, BookCopy copy) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // persist BookCopy przed dodaniem do biblioteki
            if (copy.getId() == null) {
                em.persist(copy);
            }
            copy.setLibrary(library);
            library.getBookCopies().add(copy);
            libraryRepo.update(library);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void addEmployee(Library library, Employee employee) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (employee.getId() == null) em.persist(employee);
            library.addEmployee(employee);
            libraryRepo.update(library);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void addMember(Library library, Reader reader) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (reader.getId() == null) em.persist(reader);
            library.addMember(reader);
            libraryRepo.update(library);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public Library findLibrary(UUID id) {
        return libraryRepo.find(id);
    }

    public void removeLibrary(Library library) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            libraryRepo.delete(library);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void updateLibrary(Library library) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            libraryRepo.update(library);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}
