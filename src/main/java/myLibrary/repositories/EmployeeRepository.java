package myLibrary.repositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import myLibrary.models.Employee;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class EmployeeRepository extends MongoRepository<Employee> {

    public EmployeeRepository(MongoClient client, MongoDatabase db) {
        super(client, db, "employees", Employee.class);
    }

    @Override
    public void update(Employee employee) {
        collection.replaceOne(eq("_id", employee.getId()), employee);
    }

    public List<Employee> findBySurname(String surname) {
        return collection.find(eq("surname", surname)).into(new ArrayList<>());
    }

    public List<Employee> findByPosition(String position) {
        return collection.find(eq("position", position)).into(new ArrayList<>());
    }

    public boolean existsByEmail(String email) {
        return collection.countDocuments(eq("email", email)) > 0;
    }
}
