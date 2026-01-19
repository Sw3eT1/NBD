package myLibrary.kafka;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.UpdateOptions;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.bson.Document;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.time.Duration;
import java.util.*;

/**
 * Prosta aplikacja konsumencka ("nowa aplikacja" uruchamiana jako osobny proces).
 *
 * - należy uruchomić min. dwie instancje (różne CONSUMER_INSTANCE_ID)
 * - obie należą do tej samej grupy konsumenckiej
 * - zapisuje dane w MongoDB kolekcja: rental_events
 * - zapewnia "exactly-once" na poziomie zapisu dzięki idempotentnemu upsertowi po eventId.
 */
public class RentalEventConsumerApp {

    public static void main(String[] args) {
        String bootstrap = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "kafka1:9092,kafka2:9092,kafka3:9092");
        String topic = System.getenv().getOrDefault(
                "KAFKA_RENTAL_TOPIC",
                System.getenv().getOrDefault("KAFKA_TOPIC", KafkaRentalEventProducer.DEFAULT_TOPIC)
        );
        String groupId = System.getenv().getOrDefault("KAFKA_GROUP_ID", "rental-analytics-group");
        String instanceId = System.getenv().getOrDefault("CONSUMER_INSTANCE_ID", UUID.randomUUID().toString());

        String mongoUri = System.getenv().getOrDefault(
                "MONGO_URI",
                "mongodb://admin:adminpassword@mongo1:27017/?authSource=admin&replicaSet=rs0"
        );
        String mongoDbName = System.getenv().getOrDefault("MONGO_DB", "library_analytics_db");

        System.out.println("[KAFKA][CONSUMER] starting instance=" + instanceId + " groupId=" + groupId);
        System.out.println("[KAFKA][CONSUMER] bootstrap=" + bootstrap + " topic=" + topic);
        System.out.println("[MONGO] uri=" + mongoUri + " db=" + mongoDbName);

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer-" + instanceId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "200");

        // stabilniejsze przetwarzanie
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "5000");

        Jsonb jsonb = JsonbBuilder.create();

        try (MongoClient mongoClient = MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(mongoUri))
                        .build()
        )) {
            MongoDatabase db = mongoClient.getDatabase(mongoDbName);
            MongoCollection<Document> events = db.getCollection("rental_events");

            // klucz do idempotencji
            events.createIndex(Indexes.ascending("eventId"), new IndexOptions().unique(true));

            try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {

                consumer.subscribe(List.of(topic), new org.apache.kafka.clients.consumer.ConsumerRebalanceListener() {
                    @Override
                    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                        System.out.println("[KAFKA][CONSUMER] partitions revoked: " + partitions);
                    }

                    @Override
                    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                        System.out.println("[KAFKA][CONSUMER] partitions assigned: " + partitions);
                    }
                });

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    System.out.println("[KAFKA][CONSUMER] shutdown requested");
                    consumer.wakeup();
                }));

                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                    if (records.isEmpty()) {
                        continue;
                    }

                    Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();

                    records.forEach(r -> {
                        try {
                            RentalEvent event = jsonb.fromJson(r.value(), RentalEvent.class);
                            Document doc = new Document()
                                    .append("eventId", event.getEventId())
                                    .append("rentalId", event.getRentalId())
                                    .append("readerId", event.getReaderId())
                                    .append("bookCopyId", event.getBookCopyId())
                                    .append("libraryId", event.getLibraryId())
                                    .append("libraryName", event.getLibraryName())
                                    .append("status", event.getStatus())
                                    .append("rentalDate", event.getRentalDate() != null ? event.getRentalDate().toString() : null)
                                    .append("dueDate", event.getDueDate() != null ? event.getDueDate().toString() : null)
                                    .append("createdAt", event.getCreatedAt() != null ? event.getCreatedAt().toString() : null)
                                    .append("kafka", new Document()
                                            .append("topic", r.topic())
                                            .append("partition", r.partition())
                                            .append("offset", r.offset())
                                            .append("key", r.key())
                                    );

                            // idempotentny zapis: insert tylko gdy eventId jeszcze nie było
                            events.updateOne(
                                    new Document("eventId", event.getEventId()),
                                    new Document("$setOnInsert", doc),
                                    new UpdateOptions().upsert(true)
                            );

                            System.out.println("[KAFKA][CONSUMER] stored eventId=" + event.getEventId()
                                    + " rentalId=" + event.getRentalId()
                                    + " from partition=" + r.partition() + " offset=" + r.offset());

                            offsetsToCommit.put(new TopicPartition(r.topic(), r.partition()), new OffsetAndMetadata(r.offset() + 1));
                        } catch (Exception ex) {
                            System.err.println("[KAFKA][CONSUMER] processing failed (will retry after restart): " + ex.getMessage());
                            // brak commita offsetu -> Kafka dostarczy ponownie (a Mongo zignoruje duplikat po eventId)
                        }
                    });

                    if (!offsetsToCommit.isEmpty()) {
                        consumer.commitSync(offsetsToCommit);
                    }
                }

            } catch (WakeupException we) {
                // shutdown
            }

        }
    }
}
