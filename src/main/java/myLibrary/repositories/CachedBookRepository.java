package myLibrary.repositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import myLibrary.config.RedisConnector;
import myLibrary.models.Book;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class CachedBookRepository extends BookRepository {

    private final JedisPooled redisClient;
    private final Jsonb jsonb;
    private static final String KEY_PREFIX = "book:";
    private static final long TTL_SECONDS = 600; // 10 minut

    public CachedBookRepository(MongoClient client, MongoDatabase db) {
        super(client, db); // Inicjalizacja oryginalnego repozytorium (Mongo)
        this.redisClient = RedisConnector.getPool();
        this.jsonb = JsonbBuilder.create();
    }

    // --- ODCZYT (R) z Cache ---

    @Override
    public Book findById(String id) {
        String redisKey = KEY_PREFIX + id;

        // 1. Próba odczytu z Redis
        try {
            String json = redisClient.get(redisKey);
            if (json != null) {
                // TRAFIENIE (Cache Hit)
                return jsonb.fromJson(json, Book.class);
            }
        } catch (JedisConnectionException e) {
            System.err.println("Redis failure (read): " + e.getMessage());
            // Kontynuujemy do Mongo (Fallback)
        }

        // 2. PUDŁO (Cache Miss) lub Awaria Redis -> Pobierz z Mongo
        Book book = super.findById(id);

        // 3. Zapisz w Redis (jeśli znaleziono w Mongo)
        if (book != null) {
            cacheBook(book);
        }

        return book;
    }

    // --- ZAPIS (C/U) i Inwalidacja ---

    @Override
    public void insert(Book entity) {
        super.insert(entity);
        // Przy dodawaniu nowej książki możemy ją od razu scache'ować
        cacheBook(entity);
    }

    @Override
    public void update(Book entity) {
        // 1. Aktualizacja w Mongo (Source of Truth)
        super.update(entity);

        // 2. Inwalidacja/Aktualizacja Cache
        // Strategia: Nadpisujemy cache nowymi danymi
        cacheBook(entity);
    }

    // --- USUWANIE (D) i Inwalidacja ---

    @Override
    public void delete(String id) {
        // 1. Usunięcie z Mongo
        super.delete(id);

        // 2. Unieważnienie danych w Cache (Eviction)
        try {
            redisClient.del(KEY_PREFIX + id);
        } catch (JedisConnectionException e) {
            System.err.println("Redis failure (delete): " + e.getMessage());
        }
    }

    // --- Metoda pomocnicza do zapisu w Redis ---

    private void cacheBook(Book book) {
        try {
            String json = jsonb.toJson(book);
            String key = KEY_PREFIX + book.getId();
            redisClient.set(key, json);
            redisClient.expire(key, TTL_SECONDS);
        } catch (JedisConnectionException e) {
            System.err.println("Redis failure (write): " + e.getMessage());
        }
    }

    // Metoda do ręcznego czyszczenia całego cache (wymagana w zadaniu)
    public void clearCache() {
        try {
            // UWAGA: w produkcji unikać keys*, tutaj ok do celów edukacyjnych
            var keys = redisClient.keys(KEY_PREFIX + "*");
            if (!keys.isEmpty()) {
                redisClient.del(keys.toArray(new String[0]));
            }
        } catch (Exception e) {
            System.err.println("Error clearing cache: " + e.getMessage());
        }
    }
}