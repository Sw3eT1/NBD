package myLibrary.managers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import myLibrary.Employee;
import myLibrary.Library;
import myLibrary.repositories.EmployeeRespository;

import java.util.UUID;

public class EmployeeManager {

    private final EntityManager em;
    private final EmployeeRespository empRepo;

    public EmployeeManager(EntityManager em) {
        this.em = em;
        this.empRepo = new EmployeeRespository(em);
    }

    public void addEmployee(Employee employee, Library library) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            library.addEmployee(employee);
            empRepo.add(employee);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void removeEmployee(Employee employee) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Library lib = employee.getLibrary();
            if (lib != null) lib.removeEmployee(employee);
            empRepo.delete(employee);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void updateEmployee(Employee employee) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            empRepo.update(employee);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public Employee findEmployee(UUID id) {
        return empRepo.find(id);
    }
}
