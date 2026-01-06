package myLibrary.repositories;

import com.datastax.oss.driver.api.mapper.annotations.*;
import myLibrary.models.Rental;

import java.util.List;

@Dao
public interface RentalDao {

    @Insert
    void create(Rental rental);

    @Select
    Rental findById(String readerId, String rentalId);

    @Update
    void update(Rental rental);

    @Delete
    void delete(Rental rental);

    @Select(customWhereClause = "reader_id = :readerId")
    List<Rental> findByReader(String readerId);
}