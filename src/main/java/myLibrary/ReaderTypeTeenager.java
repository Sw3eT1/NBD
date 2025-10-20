package myLibrary;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TEENAGER")
public class ReaderTypeTeenager extends ReaderType {
    public ReaderTypeTeenager() {
        super("TEENAGER", 5);
    }
}
