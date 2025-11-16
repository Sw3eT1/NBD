package myLibrary.services;

import myLibrary.models.Employee;
import myLibrary.repositories.EmployeeRepository;

import java.util.List;
import java.util.UUID;

public class EmployeeService {

    private final EmployeeRepository repo;

    public EmployeeService(EmployeeRepository repo) {
        this.repo = repo;
    }

    public void addEmployee(Employee employee) {
        if (repo.existsByEmail(employee.getEmail()))
            throw new IllegalArgumentException("Email already exists.");

        repo.insert(employee);
    }

    public void updateEmployee(Employee employee) {
        repo.update(employee);
    }

    public void deleteEmployee(UUID id) {
        repo.delete(id.toString());
    }

    public Employee getEmployee(UUID id) {
        return repo.findById(id.toString());
    }

    public List<Employee> findAll() {
        return repo.findAll();
    }

    public List<Employee> findBySurname(String surname) {
        return repo.findBySurname(surname);
    }

    public List<Employee> findByPosition(String position) {
        return repo.findByPosition(position);
    }
}
