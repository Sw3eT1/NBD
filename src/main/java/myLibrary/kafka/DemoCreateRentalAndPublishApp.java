package myLibrary.kafka;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import myLibrary.enums.BookGenre;
import myLibrary.enums.BookStatus;
import myLibrary.enums.RentalStatus;
import myLibrary.models.*;
import myLibrary.repositories.*;
import myLibrary.services.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.time.LocalDate;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Minimalna aplikacja pokazowa:
 * - tworzy dane (biblioteka, ksiazka, egzemplarz, czytelnik)
 * - tworzy nowe wypozyczenie
 * - producent Kafka (wbudowany w RentalService) publikuje je do tematu
 */
public class DemoCreateRentalAndPublishApp {

    public static void main(String[] args) {
        String mongoUri = System.getenv().getOrDefault(
                "MONGO_URI",
                "mongodb://admin:adminpassword@localhost:27017,mongo2:27017,mongo3:27017/?authSource=admin&replicaSet=rs0"
        );

        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(
                        PojoCodecProvider.builder()
                                .register(BookGenre.class)
                                .register(BookStatus.class)
                                .register(RentalStatus.class)
                                .register(ReaderType.class)
                                .automatic(true)
                                .build()
                )
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .codecRegistry(pojoCodecRegistry)
                .build();

        try (MongoClient client = MongoClients.create(settings);
             KafkaRentalEventProducer producer = KafkaRentalEventProducer.fromEnv()) {

            MongoDatabase db = client.getDatabase(System.getenv().getOrDefault("MONGO_DB", "library_demo_db"));

            db.getCollection("libraries").drop();
            db.getCollection("books").drop();
            db.getCollection("bookCopies").drop();
            db.getCollection("readers").drop();
            db.getCollection("rentals").drop();
            db.getCollection("readerTypes").drop();

            LibraryRepository libraryRepo = new LibraryRepository(client, db);
            BookRepository bookRepo = new CachedBookRepository(client, db);
            ((CachedBookRepository) bookRepo).clearCache();
            BookCopyRepository copyRepo = new BookCopyRepository(client, db);
            ReaderRepository readerRepo = new ReaderRepository(client, db);
            RentalRepository rentalRepo = new RentalRepository(client, db);
            ReaderTypeRepository readerTypeRepo = new ReaderTypeRepository(client, db);

            LibraryService libraryService = new LibraryService(libraryRepo);
            BookService bookService = new BookService(bookRepo, copyRepo);
            ReaderService readerService = new ReaderService(readerRepo, readerTypeRepo);
            ReaderTypeService readerTypeService = new ReaderTypeService(readerTypeRepo);
            BookCopyService bookCopyService = new BookCopyService(bookRepo, libraryRepo, copyRepo);

            RentalService rentalService = new RentalService(
                    rentalRepo, copyRepo, readerRepo,
                    libraryRepo,
                    producer
            );

            // --- seed minimalny ---
            Address libAddress = new Address("1", "Główna", "Warszawa", "Mazowieckie", "00-001", "Polska");
            Library lib = new Library("Biblioteka Kafka Demo", libAddress, "000", "demo@lib.pl", "demo", true, "00:00-23:59");
            libraryService.addLibrary(lib);

            Book book = new Book("Kafka w praktyce", "Autor","40925", BookGenre.SCIENCE);
            bookService.addBook(book);

            BookCopy copy = bookCopyService.createCopy(book.getId(), lib.getId());

            ReaderType type = new ReaderTypeAdult();
            readerTypeService.addReaderType(type);

            Address readerAddress = new Address("2", "Boczna", "Warszawa", "Mazowieckie", "00-002", "Polska");
            Reader reader = new Reader("Jan", "Kowalski","jan@x.pl", "111",readerAddress, lib, "2340", type);
            readerService.registerReader(reader);

            // --- nowe wypożyczenie -> Kafka ---
            rentalService.rent(reader, copy, LocalDate.now().plusDays(14));

            System.out.println("[DEMO] rental created and published to Kafka.\n" +
                    "Sprawdz logi producenta oraz uruchom konsumenta, aby zapisac zdarzenia do bazy.");
        }
    }
}
