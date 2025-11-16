package myLibrary.models;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator("KID")
public class ReaderTypeKid extends ReaderType {
    public ReaderTypeKid() { super("KID", 3); }
}
