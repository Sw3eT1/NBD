package myLibrary.models;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator("TEENAGER")
public class ReaderTypeTeenager extends ReaderType {
    public ReaderTypeTeenager() { super("TEENAGER", 5); this.setId("TEENAGER");  }
}
