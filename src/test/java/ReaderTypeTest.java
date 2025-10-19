import library.ReaderType;
import library.ReaderTypeAdult;
import library.ReaderTypeKid;
import library.ReaderTypeTeenager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReaderTypeTest {
    ReaderType readerTypeKid = new ReaderTypeKid();
    ReaderType readerTypeTeenager = new ReaderTypeTeenager();
    ReaderType readerTypeAdult = new ReaderTypeAdult();


    @Test
    void readerTypeConstructor() {
        assertEquals("KID", readerTypeKid.getName());
        assertEquals("TEENAGER", readerTypeTeenager.getName());
        assertEquals("ADULT", readerTypeAdult.getName());
    }
}