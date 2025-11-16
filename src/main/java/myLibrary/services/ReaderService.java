package myLibrary.services;

import myLibrary.models.Reader;
import myLibrary.repositories.ReaderRepository;
import myLibrary.repositories.ReaderTypeRepository;
import org.bson.Document;

import java.util.List;

public class ReaderService {

    private final ReaderRepository readerRepo;
    private final ReaderTypeRepository typeRepo;

    public ReaderService(ReaderRepository readerRepo, ReaderTypeRepository typeRepo) {
        this.readerRepo = readerRepo;
        this.typeRepo = typeRepo;
    }

    public void registerReader(Reader reader) {
        readerRepo.insert(reader);
    }

    public List<Reader> findAll() {
        return readerRepo.findAll();
    }

    public Document getReaderWithType(String readerId) {
        return readerRepo.getReaderWithType(readerId);
    }

    public void deleteReader(String id) {
        readerRepo.delete(id);
    }

}

