package myLibrary.repositories;

import com.datastax.oss.driver.api.mapper.annotations.*;
import myLibrary.models.BookCopy;

import java.util.List;

@Dao
public interface BookCopyDao {

    @Insert
    void create(BookCopy copy);

    @Select
    BookCopy findById(String libraryId, String bookId, String copyId);

    @Update
    void update(BookCopy copy);

    @Delete
    void delete(BookCopy copy);

    @Select(customWhereClause = "library_id = :libraryId AND book_id = :bookId")
    List<BookCopy> findByBook(String libraryId, String bookId);
}