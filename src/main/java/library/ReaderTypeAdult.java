package library;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADULT")
public class ReaderTypeAdult extends ReaderType {
    public ReaderTypeAdult() {
        super("ADULT", 10);
    }
}
