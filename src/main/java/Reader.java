import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Reader extends Person {
    private String cardNumber;
    private List<Rental> rentals;
    private ReaderType readerType;

    public Reader() {
        super();
        this.rentals = new ArrayList<>();
    }

    public Reader(UUID id, String name, String surname, String email, String phone, Address address,
                  String cardNumber, ReaderType readerType) {
        super(id, name, surname, email, phone, address);
        this.cardNumber = cardNumber;
        this.readerType = readerType;
        this.rentals = new ArrayList<>();
    }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public List<Rental> getRentals() { return rentals; }
    public void setRentals(List<Rental> rentals) { this.rentals = rentals; }

    public ReaderType getReaderType() { return readerType; }
    public void setReaderType(ReaderType readerType) { this.readerType = readerType; }

    @Override
    public String toString() {
        return "Reader{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", address=" + getAddress() +
                ", cardNumber='" + cardNumber + '\'' +
                ", rentals=" + rentals +
                ", readerType=" + readerType +
                '}';
    }
}
