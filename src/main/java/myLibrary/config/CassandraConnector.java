package myLibrary.config;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;

import java.net.InetSocketAddress;

public class CassandraConnector {

    private static CqlSession session;

    private CassandraConnector() {
    }

    public static CqlSession getSession() {
        if (session == null) {
            // Domyślne wartości – dobre pod docker-compose z Cassandrą na localhost
            String host = System.getenv().getOrDefault("CASSANDRA_HOST", "127.0.0.1");
            int port = Integer.parseInt(System.getenv().getOrDefault("CASSANDRA_PORT", "9042"));
            String datacenter = System.getenv().getOrDefault("CASSANDRA_DC", "dc1");
            String keyspace = System.getenv().getOrDefault("CASSANDRA_KEYSPACE", "library");

            session = CqlSession.builder()
                    .addContactPoint(new InetSocketAddress(host, port))
                    .withLocalDatacenter(datacenter)
                    .withKeyspace(CqlIdentifier.fromCql(keyspace))
                    .build();
        }
        return session;
    }

    public static void close() {
        if (session != null && !session.isClosed()) {
            session.close();
        }
    }
}