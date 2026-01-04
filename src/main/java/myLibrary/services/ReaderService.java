package myLibrary.services;

import myLibrary.models.Reader;
import myLibrary.models.ReaderType;
import myLibrary.repositories.ReaderRepository;
import myLibrary.repositories.ReaderTypeRepository;

public class ReaderService {

    private final ReaderRepository readerRepo;
    private final ReaderTypeRepository typeRepo;

    public ReaderService(ReaderRepository readerRepo,
                         ReaderTypeRepository typeRepo) {
        this.readerRepo = readerRepo;
        this.typeRepo = typeRepo;
    }

    public void registerReader(Reader reader) {
        readerRepo.insert(reader);
    }

    public Reader getReader(String libraryId, String readerId) {
        return readerRepo.findById(libraryId, readerId);
    }

    public void updateReader(Reader reader) {
        readerRepo.update(reader);
    }

    public void deleteReader(String libraryId, String readerId) {
        readerRepo.delete(libraryId, readerId);
    }

    public ReaderType getReaderType(String readerTypeId) {
        return typeRepo.findById(readerTypeId);
    }
}