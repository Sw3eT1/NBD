package myLibrary.models;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator("ADULT")
public class ReaderTypeAdult extends ReaderType {
    public ReaderTypeAdult() { super("ADULT", 10); }
}
