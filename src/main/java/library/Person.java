package library;

import jakarta.persistence.*;
import java.util.UUID;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String surname;
    private String email;
    private String phone;

    @Embedded
    private Address address;

    protected Person() {
    }

    protected Person(String name, String surname, String email, String phone, Address address) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.address = address;
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

    @Override
    public String toString() {
        return "library.Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address=" + address +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;

        return new EqualsBuilder()
                .append(getId(), person.getId())
                .append(getName(), person.getName())
                .append(getSurname(), person.getSurname())
                .append(getEmail(), person.getEmail())
                .append(getPhone(), person.getPhone())
                .append(getAddress(), person.getAddress())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getName())
                .append(getSurname())
                .append(getEmail())
                .append(getPhone())
                .append(getAddress())
                .toHashCode();
    }
}
