package library;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "Readers")
public class Reader extends Person {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private String cardNumber;


    @ManyToOne
    @JoinColumn(name = "reader_type_id")
    private ReaderType readerType;

    public Reader() {
        super();
    }

    public Reader(String name, String surname, String email, String phone, Address address,
                  Library library, String cardNumber, ReaderType readerType) {
        super(name, surname, email, phone, address, library);
        this.cardNumber = cardNumber;
        this.readerType = readerType;
    }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }


    public ReaderType getReaderType() { return readerType; }
    public void setReaderType(ReaderType readerType) { this.readerType = readerType; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Reader reader = (Reader) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(getCardNumber(), reader.getCardNumber()).append(getReaderType(), reader.getReaderType()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(getCardNumber()).append(getReaderType()).toHashCode();
    }

    @Override
    public String toString() {
        return "Reader{" +
                "cardNumber='" + cardNumber + '\'' +
                ", readerType=" + readerType +
                '}';
    }
}
