package myLibrary.config;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import myLibrary.repositories.LibraryMapper;
import myLibrary.repositories.LibraryMapperBuilder;

import java.net.InetSocketAddress;

public class CassandraConnector {

    private static CqlSession session;
    private static LibraryMapper mapper;

    private CassandraConnector() {}

    public static synchronized CqlSession getSession() {
        if (session == null || session.isClosed()) {
            session = CqlSession.builder()
                    .addContactPoint(new InetSocketAddress("localhost", 9042))
                    .withLocalDatacenter("datacenter1")
                    .withKeyspace(CqlIdentifier.fromCql("library"))
                    .build();

            mapper = new LibraryMapperBuilder(session).build();
        }
        return session;
    }

    public static synchronized LibraryMapper getMapper() {
        if (mapper == null || session == null || session.isClosed()) {
            getSession();
        }
        return mapper;
    }

    public static synchronized void close() {
        if (session != null && !session.isClosed()) {
            session.close();
        }
        session = null;
        mapper = null;
    }
}