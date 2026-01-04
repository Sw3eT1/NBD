package myLibrary.models;

import com.datastax.oss.driver.api.mapper.annotations.UDT;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.annotations.Field;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@UDT(keyspace = "library", name = "address")
@CqlName("address")
public class Address {

    @Field(name = "house_number")
    private String houseNumber;

    @Field(name = "street")
    private String street;

    @Field(name = "city")
    private String city;

    @Field(name = "state")
    private String state;

    @Field(name = "zipcode")
    private String zipcode;

    @Field(name = "country")
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