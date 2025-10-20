package myLibrary;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Libraries")
public class Library {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;

    @Embedded
    private Address address;

    private String phoneNumber;
    private String email;
    private String website;

    @OneToMany(mappedBy = "library", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookCopy> bookCopies;

    @OneToMany(mappedBy = "library", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees;

    @OneToMany(mappedBy = "library", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reader> members;


    private boolean openOnWeekends;
    private String openingHours;

    public Library(String name, Address address, String phoneNumber, String email,
                   String website,boolean openOnWeekends, String openingHours) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.website = website;
        this.bookCopies = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.members = new ArrayList<>();
        this.openOnWeekends = openOnWeekends;
        this.openingHours = openingHours;
    }

    public Library() {

    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<BookCopy> getBookCopies() {
        return bookCopies;
    }

    public void setBookCopies(List<BookCopy> bookCopies) {
        this.bookCopies = bookCopies;
    }

    public boolean isOpenOnWeekends() {
        return openOnWeekends;
    }

    public void setOpenOnWeekends(boolean openOnWeekends) {
        this.openOnWeekends = openOnWeekends;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void addEmployee(Employee employee) {
        this.employees.add(employee);
        employee.setLibrary(this);
    }

    public void removeEmployee(Employee employee) {
        this.employees.remove(employee);
        employee.setLibrary(null);
    }

    public List<Reader> getMembers(){
        return members;
    }

    public void addMember(Reader reader) {
        this.members.add(reader);
        reader.setLibrary(this);
    }

    public void removeMember(Reader reader) {
        this.members.remove(reader);
        reader.setLibrary(null);
    }


    @Override
    public String toString() {
        return "Library{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address=" + address +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", website='" + website + '\'' +
                ", bookCopies=" + bookCopies +
                ", employees=" + employees +
                ", members=" + members +
                ", openOnWeekends=" + openOnWeekends +
                ", openingHours='" + openingHours + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Library library = (Library) o;

        return new EqualsBuilder().append(isOpenOnWeekends(), library.isOpenOnWeekends()).append(getId(), library.getId()).append(getName(), library.getName()).append(getAddress(), library.getAddress()).append(getPhoneNumber(), library.getPhoneNumber()).append(getEmail(), library.getEmail()).append(getWebsite(), library.getWebsite()).append(getBookCopies(), library.getBookCopies()).append(getEmployees(), library.getEmployees()).append(getMembers(), library.getMembers()).append(getOpeningHours(), library.getOpeningHours()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getName()).append(getAddress()).append(getPhoneNumber()).append(getEmail()).append(getWebsite()).append(getBookCopies()).append(getEmployees()).append(getMembers()).append(isOpenOnWeekends()).append(getOpeningHours()).toHashCode();
    }
}
