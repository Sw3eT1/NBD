package myLibrary.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@BsonDiscriminator("READER")
public class Reader extends Person {

    private String cardNumber;

    @BsonProperty("readerTypeId")
    private String readerTypeId;

    public Reader() {}

    public Reader(String name, String surname, String email, String phone,
                  Address address, Library library,
                  String cardNumber, ReaderType type) {

        super(name, surname, email, phone, address, library);
        this.cardNumber = cardNumber;
        this.readerTypeId = type.getId();
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getReaderTypeId() {
        return readerTypeId;
    }

    public void setReaderTypeId(String readerTypeId) {
        this.readerTypeId = readerTypeId;
    }

    @Override
    public String toString() {
        return "Reader{" +
                "cardNumber='" + cardNumber + '\'' +
                ", readerTypeId='" + readerTypeId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Reader reader = (Reader) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(getCardNumber(), reader.getCardNumber()).append(getReaderTypeId(), reader.getReaderTypeId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(getCardNumber()).append(getReaderTypeId()).toHashCode();
    }
}
