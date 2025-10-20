package myLibrary;

import jakarta.persistence.*;
import java.util.UUID;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column (nullable = false)
    private String name;

    @Column (nullable = false)
    private String surname;

    @Column(nullable = false, unique = true)
    @jakarta.validation.constraints.Email
    private String email;

    @Column(nullable = false, unique = true)
    private String phone;

    @Embedded
    private Address address;

    @ManyToOne
    @JoinColumn(name = "library_id", nullable = false)
    private Library library;

    protected Person() {
    }

    protected Person(String name, String surname, String email, String phone, Address address, Library library) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.library = library;
    }

    public UUID getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public Library getLibrary() { return library; }
    public void setLibrary(Library library) { this.library = library; }


    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address=" + address +
                ", library=" + library +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return new EqualsBuilder().append(getId(), person.getId()).append(getName(), person.getName()).append(getSurname(), person.getSurname()).append(getEmail(), person.getEmail()).append(getPhone(), person.getPhone()).append(getAddress(), person.getAddress()).append(getLibrary(), person.getLibrary()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getName()).append(getSurname()).append(getEmail()).append(getPhone()).append(getAddress()).append(getLibrary()).toHashCode();
    }
}
