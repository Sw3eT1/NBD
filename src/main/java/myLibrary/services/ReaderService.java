package myLibrary.services;

import myLibrary.models.Reader;
import myLibrary.models.ReaderType;
import myLibrary.repositories.ReaderDao;
import myLibrary.repositories.ReaderTypeDao;

public class ReaderService {

    private final ReaderDao readerDao;
    private final ReaderTypeDao readerTypeDao;

    public ReaderService(ReaderDao readerRepo,
                         ReaderTypeDao typeRepo) {
        this.readerDao = readerRepo;
        this.readerTypeDao = typeRepo;
    }

    public void registerReader(Reader reader) {
        readerDao.create(reader);
    }

    public Reader getReader(String libraryId, String readerId) {
        return readerDao.findById(libraryId, readerId);
    }

    public void updateReader(Reader reader) {
        readerDao.update(reader);
    }

    public void deleteReader(Reader reader) {
        readerDao.delete(reader);
    }

    public ReaderType getReaderType(String readerTypeId) {
        return readerTypeDao.findById(readerTypeId);
    }
}