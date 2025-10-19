import java.util.UUID;

public abstract class ReaderType {
    UUID id;
    String name;

    public ReaderType() {
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
