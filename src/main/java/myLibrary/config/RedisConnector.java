package myLibrary.config;

import redis.clients.jedis.JedisPooled;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RedisConnector {
    private static JedisPooled pool;

    private RedisConnector() {}

    public static JedisPooled getPool() {
        if (pool == null) {
            Properties prop = new Properties();

            // Domyślne wartości
            String host = "localhost";
            int port = 6379;

            // 1. Najpierw spróbuj załadować z pliku (jeśli jest)
            try (InputStream input = RedisConnector.class.getClassLoader().getResourceAsStream("redis.properties")) {
                if (input != null) {
                    prop.load(input);
                    if (prop.getProperty("redis.host") != null) {
                        host = prop.getProperty("redis.host");
                    }
                    if (prop.getProperty("redis.port") != null) {
                        port = Integer.parseInt(prop.getProperty("redis.port"));
                    }
                }
            } catch (IOException e) {
                // Ignorujemy błąd pliku, używamy domyślnych
                System.err.println("Warning: Could not load redis.properties");
            }

            // 2. NADPISZ zmienną środowiskową (Dla Dockera!)
            // To jest ten fragment, którego brakowało
            String envHost = System.getenv("REDIS_HOST");
            if (envHost != null && !envHost.isEmpty()) {
                host = envHost;
            }

            // Tworzymy połączenie z ostatecznym hostem
            pool = new JedisPooled(host, port);
        }
        return pool;
    }
}