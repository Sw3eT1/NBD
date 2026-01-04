package myLibrary.config;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;

import java.net.InetSocketAddress;

public class CassandraConnector {

    private static CqlSession session;

    private CassandraConnector() {
    }

    public static synchronized CqlSession getSession() {
        if (session == null || session.isClosed()) {
            String host = System.getenv().getOrDefault("CASSANDRA_HOST", "127.0.0.1");
            int port = Integer.parseInt(System.getenv().getOrDefault("CASSANDRA_PORT", "9042"));
            String datacenter = System.getenv().getOrDefault("CASSANDRA_DC", "datacenter1");
            String keyspace = System.getenv().getOrDefault("CASSANDRA_KEYSPACE", "library");

            session = CqlSession.builder()
                    .addContactPoint(new InetSocketAddress(host, port))
                    .withLocalDatacenter(datacenter)
                    .withKeyspace(CqlIdentifier.fromCql(keyspace))
                    .build();
        }
        return session;
    }

    public static synchronized void close() {
        if (session != null && !session.isClosed()) {
            session.close();
        }
    }
}
