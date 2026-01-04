package myLibrary.models;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;

@Entity(defaultKeyspace = "library")
@CqlName("employees_by_library")
public class Employee extends Person {

    @PartitionKey
    @CqlName("library_id")
    @Override
    public String getLibraryId() {
        return super.getLibraryId();
    }

    @ClusteringColumn
    @CqlName("employee_id")
    @Override
    public String getId() {
        return super.getId();
    }

    @CqlName("name")
    @Override
    public String getName() {
        return super.getName();
    }

    @CqlName("surname")
    @Override
    public String getSurname() {
        return super.getSurname();
    }

    @CqlName("email")
    @Override
    public String getEmail() {
        return super.getEmail();
    }

    @CqlName("phone")
    @Override
    public String getPhone() {
        return super.getPhone();
    }

    @CqlName("address")
    @Override
    public Address getAddress() {
        return super.getAddress();
    }

    @CqlName("position")
    private String position;

    @CqlName("salary")
    private double salary;

    @CqlName("hire_date")
    private LocalDate hireDate;

    public Employee() {
        super();
    }

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
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Employee employee = (Employee) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(getSalary(), employee.getSalary())
                .append(getPosition(), employee.getPosition())
                .append(getHireDate(), employee.getHireDate())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getPosition())
                .append(getSalary())
                .append(getHireDate())
                .toHashCode();
    }
}