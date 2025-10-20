package myLibrary;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Readers")
public class Reader extends Person {

    @Column(nullable = false, unique = true)
    private String cardNumber;

    @ManyToOne
    @JoinColumn(name = "reader_type_id")
    private ReaderType readerType;

    @OneToMany(mappedBy = "reader", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rental> rentals = new ArrayList<>();

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

    public List<Rental> getRentals() { return rentals; }

    // 🔹 metody pomocnicze
    public void addRental(Rental rental) {
        rentals.add(rental);
        rental.setReader(this);
    }

    public void removeRental(Rental rental) {
        rentals.remove(rental);
        rental.setReader(null);
    }

    @Override
    public String toString() {
        return "Reader{" +
                "cardNumber='" + cardNumber + '\'' +
                ", readerType=" + readerType +
                ", rentals=" + rentals +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Reader reader = (Reader) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(getCardNumber(), reader.getCardNumber()).append(getReaderType(), reader.getReaderType()).append(getRentals(), reader.getRentals()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(getCardNumber()).append(getReaderType()).append(getRentals()).toHashCode();
    }
}
