package myLibrary.models;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

@Entity(defaultKeyspace = "library")
@CqlName("reader_types")
public class ReaderType {

    @PartitionKey
    @CqlName("reader_type_id")
    private String id;

    @CqlName("name")
    protected String name;

    @CqlName("max_books")
    protected int maxBooks;

    public ReaderType() {
        this.id = UUID.randomUUID().toString();
    }

    public ReaderType(String name, int maxBooks) {
        this();
        this.name = name;
        this.maxBooks = maxBooks;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getMaxBooks() { return maxBooks; }
    public void setMaxBooks(int maxBooks) { this.maxBooks = maxBooks; }

    @Override
    public String toString() {
        return "ReaderType{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", maxBooks=" + maxBooks +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ReaderType that = (ReaderType) o;

        return new EqualsBuilder()
                .append(getMaxBooks(), that.getMaxBooks())
                .append(getId(), that.getId())
                .append(getName(), that.getName())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getName())
                .append(getMaxBooks())
                .toHashCode();
    }
}