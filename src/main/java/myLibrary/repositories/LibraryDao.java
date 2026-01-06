package myLibrary.repositories;

import com.datastax.oss.driver.api.mapper.annotations.*;
import myLibrary.models.Library;

@Dao
public interface LibraryDao {

    @Insert
    void create(Library library);

    @Select
    Library findById(String libraryId);

    @Update
    void update(Library library);

    @Delete
    void delete(Library library);
}