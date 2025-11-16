package myLibrary.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;

import java.util.UUID;

@BsonDiscriminator
public class Library {

    @BsonId
    private String id;

    private String name;
    private Address address;

    private String phoneNumber;
    private String email;
    private String website;

    private boolean openOnWeekends;
    private String openingHours;

    public Library() {
        this.id = UUID.randomUUID().toString();
    }

    public Library(String name, Address address, String phoneNumber,
                   String email, String website,
                   boolean openOnWeekends, String openingHours) {

        this();
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.website = website;
        this.openOnWeekends = openOnWeekends;
        this.openingHours = openingHours;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    public String toString() {
        return "Library{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address=" + address +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", website='" + website + '\'' +
                ", openOnWeekends=" + openOnWeekends +
                ", openingHours='" + openingHours + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Library library = (Library) o;

        return new EqualsBuilder().append(isOpenOnWeekends(), library.isOpenOnWeekends()).append(getId(), library.getId()).append(getName(), library.getName()).append(getAddress(), library.getAddress()).append(getPhoneNumber(), library.getPhoneNumber()).append(getEmail(), library.getEmail()).append(getWebsite(), library.getWebsite()).append(getOpeningHours(), library.getOpeningHours()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getName()).append(getAddress()).append(getPhoneNumber()).append(getEmail()).append(getWebsite()).append(isOpenOnWeekends()).append(getOpeningHours()).toHashCode();
    }
}
