package library;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "libraries")
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

    @OneToMany(mappedBy = "libraryId", cascade = CascadeType.ALL)
    private List<BookCopy> bookCopies;

    @OneToMany(mappedBy = "libraryId", cascade = CascadeType.ALL)
    private List<Employee> staff;

    @OneToMany(mappedBy = "libraryId", cascade = CascadeType.ALL)
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
        this.bookCopies = new ArrayList<BookCopy>();
        this.staff = new ArrayList<Employee>();
        this.members = new ArrayList<Reader>();
        this.openOnWeekends = openOnWeekends;
        this.openingHours = openingHours;
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

    public List<Employee> getStaff() {
        return staff;
    }

    public void setStaff(List<Employee> staff) {
        this.staff = staff;
    }

    public List<Reader> getMembers() {
        return members;
    }

    public void setMembers(List<Reader> members) {
        this.members = members;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Library library = (Library) o;

        return new EqualsBuilder().append(isOpenOnWeekends(), library.isOpenOnWeekends()).append(getId(), library.getId()).append(getName(), library.getName()).append(getAddress(), library.getAddress()).append(getPhoneNumber(), library.getPhoneNumber()).append(getEmail(), library.getEmail()).append(getWebsite(), library.getWebsite()).append(getBookCopies(), library.getBookCopies()).append(getStaff(), library.getStaff()).append(getMembers(), library.getMembers()).append(getOpeningHours(), library.getOpeningHours()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getName()).append(getAddress()).append(getPhoneNumber()).append(getEmail()).append(getWebsite()).append(getBookCopies()).append(getStaff()).append(getMembers()).append(isOpenOnWeekends()).append(getOpeningHours()).toHashCode();
    }

    @Override
    public String toString() {
        return "library.Library{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address=" + address +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", website='" + website + '\'' +
                ", bookCopies=" + bookCopies +
                ", staff=" + staff +
                ", members=" + members +
                ", openOnWeekends=" + openOnWeekends +
                ", openingHours='" + openingHours + '\'' +
                '}';
    }
}
