package myLibrary.managers;

import jakarta.persistence.EntityManager;
import myLibrary.Reader;
import myLibrary.ReaderType;

import java.util.List;

public class ReaderManager extends BaseManager {

    public ReaderManager(EntityManager em) {
        super(em);
    }

    public void addReaderType(ReaderType type) {
        executeInTransaction(() -> {
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
        });
    }

    public void registerReader(Reader reader) {
        executeInTransaction(() -> {
            if (reader.getReaderType() != null) {
                ReaderType existingType = em.createQuery(
                                "SELECT rt FROM ReaderType rt WHERE rt.name = :name", ReaderType.class)
                        .setParameter("name", reader.getReaderType().getName())
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
                if (existingType != null) reader.setReaderType(existingType);
                else em.persist(reader.getReaderType());
            }

            Reader existingReader = em.createQuery(
                            "SELECT r FROM Reader r WHERE r.email = :email", Reader.class)
                    .setParameter("email", reader.getEmail())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (existingReader == null) em.persist(reader);
            else System.out.println("Czytelnik z tym e-mailem już istnieje.");
        });
    }

    public List<Reader> getAllReaders() {
        return em.createQuery("SELECT r FROM Reader r", Reader.class).getResultList();
    }

    public Reader findByCardNumber(String cardNumber) {
        return em.createQuery("SELECT r FROM Reader r WHERE r.cardNumber = :card", Reader.class)
                .setParameter("card", cardNumber)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public void updateReader(Reader reader) {
        executeInTransaction(() -> em.merge(reader));
    }

    public void removeReader(Reader reader) {
        executeInTransaction(() -> {
            Reader managed = reader;
            if (!em.contains(managed)) {
                managed = em.merge(managed);
            }
            em.remove(managed);
        });
    }

}

