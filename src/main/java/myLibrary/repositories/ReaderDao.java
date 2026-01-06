package myLibrary.repositories;

import com.datastax.oss.driver.api.mapper.annotations.*;
import myLibrary.models.Reader;

import java.util.List;

@Dao
public interface ReaderDao {

    @Insert
    void create(Reader reader);

    @Select
    Reader findById(String libraryId, String readerId);

    @Update
    void update(Reader reader);

    @Delete
    void delete(Reader reader);

    @Select(customWhereClause = "library_id = :libraryId")
    List<Reader> findByLibrary(String libraryId);
}