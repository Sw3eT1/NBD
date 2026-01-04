package myLibrary.services;

import myLibrary.models.Employee;
import myLibrary.repositories.EmployeeRepository;

public class EmployeeService {

    private final EmployeeRepository repo;

    public EmployeeService(EmployeeRepository repo) {
        this.repo = repo;
    }

    public void addEmployee(Employee employee) {
        // W Cassandrze brak łatwego sprawdzania unikalności email,
        // więc pomijamy walidację existsByEmail z Mongo.
        repo.insert(employee);
    }

    public void updateEmployee(Employee employee) {
        repo.update(employee);
    }

    public void deleteEmployee(String libraryId, String employeeId) {
        repo.delete(libraryId, employeeId);
    }

    public Employee getEmployee(String libraryId, String employeeId) {
        return repo.findById(libraryId, employeeId);
    }
}