package myLibrary.models;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity(defaultKeyspace = "library")
@CqlName("readers_by_library")
public class Reader extends Person {

    @PartitionKey
    @CqlName("library_id")
    @Override
    public String getLibraryId() {
        return super.getLibraryId();
    }

    @ClusteringColumn
    @CqlName("reader_id")
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

    @CqlName("card_number")
    private String cardNumber;

    @CqlName("reader_type_id")
    private String readerTypeId;

    @CqlName("active_rentals")
    private int activeRentals = 0;

    public Reader() {
        super();
    }

    public Reader(String name, String surname, String email, String phone,
                  Address address, Library library,
                  String cardNumber, ReaderType type) {

        super(name, surname, email, phone, address, library);
        this.cardNumber = cardNumber;
        this.readerTypeId = type.getId();
    }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getReaderTypeId() { return readerTypeId; }
    public void setReaderTypeId(String readerTypeId) { this.readerTypeId = readerTypeId; }

    public int getActiveRentals() { return activeRentals; }
    public void setActiveRentals(int activeRentals) { this.activeRentals = activeRentals; }

    @Override
    public String toString() {
        return "Reader{" +
                "cardNumber='" + cardNumber + '\'' +
                ", readerTypeId='" + readerTypeId + '\'' +
                ", activeRentals=" + activeRentals +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Reader reader = (Reader) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(getActiveRentals(), reader.getActiveRentals())
                .append(getCardNumber(), reader.getCardNumber())
                .append(getReaderTypeId(), reader.getReaderTypeId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getCardNumber())
                .append(getReaderTypeId())
                .append(getActiveRentals())
                .toHashCode();
    }
}