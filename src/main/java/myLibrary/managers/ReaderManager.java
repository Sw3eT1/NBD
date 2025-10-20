package myLibrary.managers;

import jakarta.persistence.EntityManager;
import myLibrary.Reader;
import myLibrary.ReaderType;

import java.util.List;

public class ReaderManager {
    private final EntityManager em;

    public ReaderManager(EntityManager em) {
        this.em = em;
    }

    // Dodaje typ czytelnika do bazy
    public void addReaderType(ReaderType type) {
        em.getTransaction().begin();
        try {
            // Szukamy typu po nazwie
            ReaderType existing = em.createQuery(
                            "SELECT rt FROM ReaderType rt WHERE rt.name = :name", ReaderType.class)
                    .setParameter("name", type.getName())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (existing == null) {
                em.persist(type);
                System.out.println("Dodano nowy typ czytelnika: " + type.getName());
            } else {
                System.out.println("Typ czytelnika już istnieje: " + existing.getName());
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }


    // Rejestruje czytelnika, upewniając się że typ jest już w bazie
    public void registerReader(Reader reader) {
        em.getTransaction().begin();
        try {
            // --- Sprawdzenie typu czytelnika ---
            if (reader.getReaderType() != null) {
                ReaderType existingType = em.createQuery(
                                "SELECT rt FROM ReaderType rt WHERE rt.name = :name", ReaderType.class)
                        .setParameter("name", reader.getReaderType().getName())
                        .getResultStream()
                        .findFirst()
                        .orElse(null);

                if (existingType != null) {
                    reader.setReaderType(existingType);
                } else {
                    em.persist(reader.getReaderType());
                }
            }

            // --- Sprawdzenie czytelników po e-mailu ---
            Reader existingReader = em.createQuery(
                            "SELECT r FROM Reader r WHERE r.email = :email", Reader.class)
                    .setParameter("email", reader.getEmail())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (existingReader != null) {
                System.out.println("Czytelnik z tym e-mailem już istnieje: " + reader.getEmail());
            } else {
                em.persist(reader);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }


    public List<Reader> getAllReaders() {
        return em.createQuery("SELECT r FROM Reader r", Reader.class).getResultList();
    }

    // --- Dodatkowe funkcje ---
    public Reader findByCardNumber(String cardNumber) {
        return em.createQuery("SELECT r FROM Reader r WHERE r.cardNumber = :card", Reader.class)
                .setParameter("card", cardNumber)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public void updateReader(Reader reader) {
        em.getTransaction().begin();
        em.merge(reader);
        em.getTransaction().commit();
    }

    public void removeReader(Reader reader) {
        em.getTransaction().begin();
        if (!em.contains(reader)) {
            reader = em.merge(reader);
        }
        em.remove(reader);
        em.getTransaction().commit();
    }
}
