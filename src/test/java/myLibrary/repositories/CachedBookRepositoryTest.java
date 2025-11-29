package myLibrary.repositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import myLibrary.enums.BookGenre;
import myLibrary.models.Book;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CachedBookRepositoryTest {

    private CachedBookRepository repository;

    @BeforeEach
    void setUp() {
        // 1. Konfiguracja Codeca (obsługa klas POJO)
        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        // 2. Dynamiczne ustalanie adresu bazy (Docker vs IntelliJ)
        String mongoHost = System.getenv("MONGO_HOST");
        // Jeśli zmienna nie jest ustawiona (np. w IntelliJ), użyj localhost
        if (mongoHost == null || mongoHost.isEmpty()) {
            mongoHost = "localhost";
        }

        // 3. Budowanie Connection String
        // Format: mongodb://user:pass@HOST:27017/...
        String connectionUrl = String.format(
                "mongodb://admin:adminpassword@%s:27017/?authSource=admin&directConnection=true",
                mongoHost
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionUrl))
                .codecRegistry(pojoCodecRegistry)
                .build();

        // 4. Tworzenie klienta
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase db = mongoClient.getDatabase("libraryTestDb");

        // 5. Inicjalizacja repozytorium
        repository = new CachedBookRepository(mongoClient, db);

        // 6. Czyszczenie przed testem
        repository.clearCache();
        db.getCollection("books").drop();
    }

    @Test
    void shouldAddBookToRedisOnInsert() {
        // Given
        Book book = new Book("Wiedźmin", "Sapkowski", "123456", BookGenre.FANTASY);

        // When
        repository.insert(book);

        // Then
        Book retrieved = repository.findById(book.getId());
        assertEquals(book.getTitle(), retrieved.getTitle());
    }

    @Test
    void shouldRetrieveFromCacheAfterFirstRead() {
        // Given
        Book book = new Book("Diuna", "Herbert", "987654", BookGenre.SCIENCE_FICTION);
        repository.insert(book);

        // Pierwszy odczyt (zapisuje do cache)
        repository.findById(book.getId());

        // When
        long start = System.nanoTime();
        Book cachedBook = repository.findById(book.getId());
        long duration = System.nanoTime() - start;

        // Then
        assertNotNull(cachedBook);
        assertEquals("Diuna", cachedBook.getTitle());
        System.out.println("Read time (ns): " + duration);
    }

    @Test
    void shouldInvalidateCacheOnUpdate() {
        // Given
        Book book = new Book("Stary Tytuł", "Autor", "111", BookGenre.CLASSIC);
        repository.insert(book);

        // When
        book.setTitle("Nowy Tytuł");
        repository.update(book);

        Book updatedBook = repository.findById(book.getId());

        // Then
        assertEquals("Nowy Tytuł", updatedBook.getTitle());
    }
}