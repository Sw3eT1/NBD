package myLibrary.repositories;

import jakarta.persistence.EntityManager;
import myLibrary.Employee;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class EmployeeRespository implements Repository<Employee> {

    private final EntityManager em;

    public EmployeeRespository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void add(Employee employee) {
        em.persist(employee);
    }

    @Override
    public void delete(Employee employee) {
        em.remove(employee);
    }

    @Override
    public void update(Employee employee) {
        em.merge(employee);
    }

    @Override
    public Employee find(UUID id) {
        List<Employee> employees = em.createQuery("SELECT e from Employee e where e.id = :id", Employee.class)
                .setParameter("id", id).
                getResultList();
        return employees.isEmpty() ? null : employees.getFirst();
    }

    @Override
    public List<Employee> findAll() {
        return em.createQuery("SELECT e FROM Employee e", Employee.class)
                .getResultList();
    }

    public List<Employee> findBySurname(String surname) {
        return em.createQuery("SELECT e FROM Employee e WHERE e.surname = :surname", Employee.class)
                .setParameter("surname", surname)
                .getResultList();
    }

    public List<Employee> findByPosition(String position) {
        return em.createQuery("SELECT e FROM Employee e WHERE e.position = :position", Employee.class)
                .setParameter("position", position)
                .getResultList();
    }

    public List<Employee> findBySalaryGreaterThan(double minSalary) {
        return em.createQuery("SELECT e FROM Employee e WHERE e.salary > :minSalary", Employee.class)
                .setParameter("minSalary", minSalary)
                .getResultList();
    }

    public List<Employee> findHiredAfter(LocalDate date) {
        return em.createQuery("SELECT e FROM Employee e WHERE e.hireDate > :date", Employee.class)
                .setParameter("date", date)
                .getResultList();
    }

    public boolean existsByEmail(String email) {
        Long count = em.createQuery("SELECT COUNT(e) FROM Employee e WHERE e.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

}
