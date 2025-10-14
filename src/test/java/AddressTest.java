import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    Address address = new Address("1", "Sternicza", "Lodz", "lodzkie", "91158",
            "Poland");
    @Test
    void AddressConstructor() {
        assertEquals("1", address.getHouseNumber());
        assertEquals("Sternicza", address.getStreet());
        assertEquals("Lodz", address.getCity());
        assertEquals("lodzkie", address.getState());
        assertEquals("91158", address.getZipcode());
        assertEquals("Poland", address.getCountry());
    }

    @Test
    void setters() {
        address.setHouseNumber("23");
        address.setStreet("Wodna");
        address.setCity("Pabianice");
        address.setState("mazowieckie");
        address.setZipcode("92786");
        address.setCountry("Moldavia");


        assertEquals("23", address.getHouseNumber());
        assertEquals("Wodna", address.getStreet());
        assertEquals("Pabianice", address.getCity());
        assertEquals("mazowieckie", address.getState());
        assertEquals("92786", address.getZipcode());
        assertEquals("Moldavia", address.getCountry());
    }
}