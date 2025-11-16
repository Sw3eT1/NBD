package myLibrary.services;

import myLibrary.models.ReaderType;
import myLibrary.repositories.MongoRepository;

import java.util.List;
import java.util.UUID;

public class ReaderTypeService {

    private final MongoRepository<ReaderType> repo;

    public ReaderTypeService(MongoRepository<ReaderType> repo) {
        this.repo = repo;
    }

    public void addReaderType(ReaderType type) {
        repo.insert(type);
    }

    public void updateReaderType(ReaderType type) {
        repo.update(type);
    }

    public void deleteReaderType(UUID id) {
        repo.delete(id.toString());
    }

    public ReaderType getReaderType(UUID id) {
        return repo.findById(id.toString());
    }

    public List<ReaderType> findAll() {
        return repo.findAll();
    }
}
