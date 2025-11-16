package myLibrary.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.time.LocalDate;

@BsonDiscriminator("EMPLOYEE")
public class Employee extends Person {

    private String position;
    private double salary;
    private LocalDate hireDate;

    public Employee() {}

    public Employee(String name, String surname, String email, String phone,
                    Address address, Library library,
                    String position, double salary, LocalDate hireDate) {

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
        return "Employee{" +
                "position='" + position + '\'' +
                ", salary=" + salary +
                ", hireDate=" + hireDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Employee employee = (Employee) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(getSalary(), employee.getSalary()).append(getPosition(), employee.getPosition()).append(getHireDate(), employee.getHireDate()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(getPosition()).append(getSalary()).append(getHireDate()).toHashCode();
    }
}
