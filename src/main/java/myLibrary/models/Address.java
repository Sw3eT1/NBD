package myLibrary.models;

import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@CqlName("address")
@SchemaHint(targetElement = SchemaHint.TargetElement.UDT)
public class Address {

    @CqlName("house_number")
    private String houseNumber;

    @CqlName("street")
    private String street;

    @CqlName("city")
    private String city;

    @CqlName("state")
    private String state;

    @CqlName("zipcode")
    private String zipcode;

    @CqlName("country")
    private String country;

    public Address() {
    }

    public Address(String houseNumber, String street, String city,
                   String state, String zipcode, String country) {
        this.houseNumber = houseNumber;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.country = country;
    }

    public String getHouseNumber() { return houseNumber; }
    public void setHouseNumber(String houseNumber) { this.houseNumber = houseNumber; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipcode() { return zipcode; }
    public void setZipcode(String zipcode) { this.zipcode = zipcode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    @Override
    public String toString() {
        return "Address{" +
                "houseNumber='" + houseNumber + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipcode='" + zipcode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        return new EqualsBuilder()
                .append(getHouseNumber(), address.getHouseNumber())
                .append(getStreet(), address.getStreet())
                .append(getCity(), address.getCity())
                .append(getState(), address.getState())
                .append(getZipcode(), address.getZipcode())
                .append(getCountry(), address.getCountry())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getHouseNumber())
                .append(getStreet())
                .append(getCity())
                .append(getState())
                .append(getZipcode())
                .append(getCountry())
                .toHashCode();
    }
}