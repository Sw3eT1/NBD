package myLibrary.repositories;

import jakarta.persistence.EntityManager;
import myLibrary.Book;
import myLibrary.Reader;
import myLibrary.ReaderType;

import java.util.List;
import java.util.UUID;

public class ReaderRepository implements Repository<Reader> {
    private final EntityManager em;

    public ReaderRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void add(Reader reader) {
        em.persist(reader);
    }

    @Override
    public void delete(Reader reader) {
        em.remove(reader);
    }

    @Override
    public void update(Reader reader) {
        em.merge(reader);
    }

    @Override
    public Reader find(UUID id) {
        List<Reader> readers = em.createQuery("SELECT r from Reader r where r.id = :id", Reader.class)
                .setParameter("id", id).
                getResultList();
        return readers.isEmpty() ? null : readers.getFirst();
    }

    @Override
    public List<Reader> findAll() {
        return em.createQuery("SELECT r FROM Reader r", Reader.class)
                .getResultList();
    }

    public Reader findCardNumber(String cardNumber) {
        List<Reader> readers = em.createQuery("SELECT r FROM Reader r WHERE r.cardNumber = :cardNumber", Reader.class)
                .setParameter("cardNumber", cardNumber)
                .getResultList();
        return readers.isEmpty() ? null : readers.getFirst();
    }

    public List<Reader> findBySurname(String surname) {
        return em.createQuery("SELECT r FROM Reader r WHERE r.surname = :surname", Reader.class)
                .setParameter("surname", surname)
                .getResultList();
    }

    public Reader findByEmail(String email) {
        List<Reader> result = em.createQuery("SELECT r FROM Reader r WHERE r.email = :email", Reader.class)
                .setParameter("email", email)
                .getResultList();
        return result.isEmpty() ? null : result.getFirst();
    }

    public boolean existsByCardNumber(String cardNumber) {
        Long count = em.createQuery("SELECT COUNT(r) FROM Reader r WHERE r.cardNumber = :cardNumber", Long.class)
                .setParameter("cardNumber", cardNumber)
                .getSingleResult();
        return count > 0;
    }


    public List<Reader> findByReaderType(ReaderType type) {
        return em.createQuery("SELECT r FROM Reader r WHERE r.readerType = :type", Reader.class)
                .setParameter("type", type)
                .getResultList();
    }

}
