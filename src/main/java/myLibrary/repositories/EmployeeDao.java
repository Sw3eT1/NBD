package myLibrary.repositories;

import com.datastax.oss.driver.api.mapper.annotations.*;
import myLibrary.models.Employee;

import java.util.List;

@Dao
public interface EmployeeDao {

    @Insert
    void create(Employee employee);

    @Select
    Employee findById(String libraryId, String employeeId);

    @Update
    void update(Employee employee);

    @Delete
    void delete(Employee employee);

    @Select(customWhereClause = "library_id = :libraryId")
    List<Employee> findByLibrary(String libraryId);
}