package myLibrary.services;

import myLibrary.models.Employee;
import myLibrary.repositories.EmployeeDao;

public class EmployeeService {

    private final EmployeeDao employeeDao;

    public EmployeeService(EmployeeDao repo) {
        this.employeeDao = repo;
    }

    public void addEmployee(Employee employee) {
        employeeDao.create(employee);
    }

    public void updateEmployee(Employee employee) {
        employeeDao.update(employee);
    }

    public void deleteEmployee(Employee employee) {
        employeeDao.delete(employee);
    }

    public Employee getEmployee(String libraryId, String employeeId) {
        return employeeDao.findById(libraryId, employeeId);
    }
}