package myLibrary.kafka;

import myLibrary.models.Rental;

/**
 * Abstrakcja producenta zdarzeń o nowym wypożyczeniu.
 * Implementacja Kafka znajduje się w {@link KafkaRentalEventProducer}.
 */
public interface RentalEventProducer {

    /**
     * Publikuje zdarzenie o nowym wypożyczeniu.
     *
     * @param rental      utworzone wypożyczenie
     * @param libraryName nazwa biblioteki (wypożyczalni)
     */
    void publishNewRental(Rental rental, String libraryId, String libraryName);
}
