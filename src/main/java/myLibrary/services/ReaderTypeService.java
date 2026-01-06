package myLibrary.services;

import myLibrary.models.ReaderType;
import myLibrary.repositories.ReaderTypeDao;

public class ReaderTypeService {

    private final ReaderTypeDao readerTypeDao;

    public ReaderTypeService(ReaderTypeDao repo) {
        this.readerTypeDao = repo;
    }

    public void addReaderType(ReaderType type) {
        readerTypeDao.create(type);
    }

    public void updateReaderType(ReaderType type) {
        readerTypeDao.update(type);
    }

    public void deleteReaderType(ReaderType type) {
        readerTypeDao.delete(type);
    }

    public ReaderType getReaderType(String id) {
        return readerTypeDao.findById(id);
    }
}