package myLibrary.repositories;

import java.util.UUID;

public interface Repository<T> {
    void add(T object);
    void delete(T object);
    void update(T object);
    T find(UUID id);
}
