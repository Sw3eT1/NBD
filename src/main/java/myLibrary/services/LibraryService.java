package myLibrary.services;

import myLibrary.models.Library;
import myLibrary.repositories.LibraryDao;

public class LibraryService {

    private final LibraryDao libraryDao;

    public LibraryService(LibraryDao repo) {
        this.libraryDao = repo;
    }

    public void addLibrary(Library library) {
        libraryDao.create(library);
    }

    public void update(Library library) {
        libraryDao.update(library);
    }

    public Library find(String id) {
        return libraryDao.findById(id);
    }

    public void delete(Library library) {
        libraryDao.delete(library);
    }
}