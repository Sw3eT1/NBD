package myLibrary.repositories;

import jakarta.persistence.EntityManager;
import myLibrary.Library;

import java.util.List;
import java.util.UUID;

public class LibraryRespository implements Repository<Library> {

    private final EntityManager em;

    public LibraryRespository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void add(Library library) {
        em.persist(library);
    }

    @Override
    public void delete(Library library) {
        em.remove(library);
    }

    @Override
    public void update(Library library) {
        em.merge(library);
    }

    @Override
    public Library find(UUID id) {
        List<Library> libraries = em.createQuery("SELECT l from Library l where l.id = :id", Library.class)
                .setParameter("id", id).
                getResultList();
        return libraries.isEmpty() ? null : libraries.getFirst();
    }

    public List<Library> findAll() {
        return em.createQuery("SELECT l FROM Library l", Library.class)
                .getResultList();
    }

    public List<Library> findByName(String name) {
        return em.createQuery("SELECT l FROM Library l WHERE LOWER(l.name) LIKE LOWER(:name)", Library.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    public List<Library> findOpenOnWeekends() {
        return em.createQuery("SELECT l FROM Library l WHERE l.openOnWeekends = true", Library.class)
                .getResultList();
    }

    public List<Library> findByCity(String city) {
        return em.createQuery("SELECT l FROM Library l WHERE l.address.city = :city", Library.class)
                .setParameter("city", city)
                .getResultList();
    }

    public List<Library> findByBookCopy(UUID bookId) {
        return em.createQuery("SELECT l FROM Library l JOIN l.bookCopies bc WHERE bc.book.id = :bookId", Library.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

}
