package myLibrary.repositories;

import com.datastax.oss.driver.api.mapper.annotations.*;
import myLibrary.models.Book;

@Dao
public interface BookDao {

    @Insert
    void create(Book book);

    @Select
    Book findById(String bookId);

    @Update
    void update(Book book);

    @Delete
    void delete(Book book);
}