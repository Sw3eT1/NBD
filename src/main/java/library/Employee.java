package library;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;


@Entity
@Table(name = "Employees")
public class Employee extends Person {
    private String position;
    private double salary;
    private LocalDate hireDate;

    public Employee() {
        super();
    }

    public Employee(String name, String surname, String email, String phone, Address address
                    ,Library library,String position, double salary, LocalDate hireDate) {
        super(name, surname, email, phone, address, library);
        this.position = position;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    @Override
    public String toString() {
        return "library.Employee{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", address=" + getAddress() +
                ", position='" + position + '\'' +
                ", salary=" + salary +
                ", hireDate=" + hireDate +
                '}';
    }
}
