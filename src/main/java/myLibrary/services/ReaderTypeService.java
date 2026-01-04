package myLibrary.services;

import myLibrary.models.ReaderType;
import myLibrary.repositories.ReaderTypeRepository;

public class ReaderTypeService {

    private final ReaderTypeRepository repo;

    public ReaderTypeService(ReaderTypeRepository repo) {
        this.repo = repo;
    }

    public void addReaderType(ReaderType type) {
        repo.insert(type);
    }

    public void updateReaderType(ReaderType type) {
        repo.update(type);
    }

    public void deleteReaderType(String id) {
        repo.delete(id);
    }

    public ReaderType getReaderType(String id) {
        return repo.findById(id);
    }
}