package myLibrary.repositories;

import com.datastax.oss.driver.api.mapper.annotations.*;
import myLibrary.models.ReaderType;

@Dao
public interface ReaderTypeDao {

    @Insert
    void create(ReaderType type);

    @Select
    ReaderType findById(String id);

    @Update
    void update(ReaderType type);

    @Delete
    void delete(ReaderType type);
}