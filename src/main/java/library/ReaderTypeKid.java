package library;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("KID")
public class ReaderTypeKid extends ReaderType {
    public ReaderTypeKid() {
        super("KID", 3);
    }
}
