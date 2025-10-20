package myLibrary.managers;

import jakarta.persistence.EntityManager;
import myLibrary.Employee;
import myLibrary.Library;
import myLibrary.repositories.EmployeeRespository;

import java.util.UUID;

public class EmployeeManager extends BaseManager {
    private final EmployeeRespository empRepo;

    public EmployeeManager(EntityManager em) {
        super(em);
        this.empRepo = new EmployeeRespository(em);
    }

    public void addEmployee(Employee employee, Library library) {
        executeInTransaction(() -> {
            library.addEmployee(employee);
            empRepo.add(employee);
        });
    }

    public void removeEmployee(Employee employee) {
        executeInTransaction(() -> {
            Library lib = employee.getLibrary();
            if (lib != null) lib.removeEmployee(employee);
            empRepo.delete(employee);
        });
    }

    public void updateEmployee(Employee employee) {
        executeInTransaction(() -> empRepo.update(employee));
    }

    public Employee findEmployee(UUID id) {
        return empRepo.find(id);
    }
}

