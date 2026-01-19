package myLibrary.kafka;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import myLibrary.models.Rental;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * Producent Kafka wysyłający zdarzenia o nowo utworzonych wypożyczeniach.
 */
public class KafkaRentalEventProducer implements RentalEventProducer, AutoCloseable {

    public static final String DEFAULT_TOPIC = "wypozyczenia-rezerwacje";

    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final Jsonb jsonb = JsonbBuilder.create();

    public KafkaRentalEventProducer(String bootstrapServers, String topic) {
        this.topic = (topic == null || topic.isBlank()) ? DEFAULT_TOPIC : topic;

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Idempotent producer (bez duplikacji na poziomie brokera przy retry)
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));

        // Bezpieczniejsze ustawienia dla małych wiadomości
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "120000");
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "30000");

        this.producer = new KafkaProducer<>(props);
    }

    public static KafkaRentalEventProducer fromEnv() {
        String bootstrap = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
        String topic = System.getenv().getOrDefault("KAFKA_RENTAL_TOPIC", DEFAULT_TOPIC);
        return new KafkaRentalEventProducer(bootstrap, topic);
    }

    @Override
    public void publishNewRental(Rental rental, String libraryId, String libraryName) {
        RentalEvent event = RentalEvent.from(rental, libraryId, libraryName);
        String payload = jsonb.toJson(event);

        // Klucz = rentalId -> stabilne partycjonowanie
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, rental.getId(), payload);

        producer.send(record, (meta, ex) -> {
            if (ex != null) {
                System.err.println("[KAFKA][PRODUCER] send failed: " + ex.getMessage());
            } else {
                System.out.println("[KAFKA][PRODUCER] sent rentalId=" + rental.getId()
                        + " to " + meta.topic() + " partition=" + meta.partition() + " offset=" + meta.offset());
            }
        });
        producer.flush();
    }

    @Override
    public void close() {
        producer.close();
    }
}
