package myLibrary.benchmark;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import myLibrary.enums.BookGenre;
import myLibrary.models.Book;
import myLibrary.repositories.CachedBookRepository;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS) // Mierzymy w mikrosekundach
public class CacheBenchmark {

    private CachedBookRepository repository;
    private String bookId;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Konfiguracja Codeca
        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        // 2. Dynamiczne ustalanie adresu bazy (Docker vs IntelliJ)
        String mongoHost = System.getenv("MONGO_HOST");
        if (mongoHost == null || mongoHost.isEmpty()) {
            mongoHost = "localhost";
        }

        // 3. Budowanie Connection String
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
        MongoDatabase db = mongoClient.getDatabase("benchmarkDb");

        // Wyczyść bazę testową przed benchmarkiem
        db.getCollection("books").drop();

        repository = new CachedBookRepository(mongoClient, db);
        // Wyczyść Redis
        repository.clearCache();

        // 5. Przygotowanie danych
        Book book = new Book("Benchmark Book", "Tester", "0000", BookGenre.FANTASY);
        repository.insert(book);
        bookId = book.getId();

        // Rozgrzewka cache
        repository.findById(bookId);
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        if (bookId != null) {
            repository.delete(bookId);
        }
    }

    // SCENARIUSZ 1: Cache Hit
    @Benchmark
    public Book measureCacheHit() {
        return repository.findById(bookId);
    }

    // SCENARIUSZ 2: Cache Miss
    @Benchmark
    public Book measureCacheMiss() {
        repository.clearCache();
        return repository.findById(bookId);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(CacheBenchmark.class.getSimpleName())
                .forks(0) // <--- KLUCZOWA ZMIANA: 0 oznacza brak nowego procesu, co naprawia błąd ClassNotFound
                .warmupIterations(2)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }
}