package myLibrary.services;

import myLibrary.models.Library;
import myLibrary.repositories.LibraryRepository;
import org.bson.types.ObjectId;

public class LibraryService {

    private final LibraryRepository repo;

    public LibraryService(LibraryRepository repo) {
        this.repo = repo;
    }

    public void addLibrary(Library library) {
        repo.insert(library);
    }

    public void update(Library library) {
        repo.update(library);
    }

    public Library find(String id) {
        return repo.findById(id);
    }
}
