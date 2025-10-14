import java.util.UUID;

public abstract class ReaderType {
    UUID id;
    String name;

    public ReaderType() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
