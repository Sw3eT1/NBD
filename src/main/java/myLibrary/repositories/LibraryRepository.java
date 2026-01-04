package myLibrary.repositories;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import myLibrary.models.Address;
import myLibrary.models.Library;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.data.UdtValue;


public class LibraryRepository {

    private final CqlSession session;
    private final UserDefinedType addressUdt;
    private final PreparedStatement insertStmt;
    private final PreparedStatement selectByIdStmt;
    private final PreparedStatement deleteStmt;

    public LibraryRepository(CqlSession session) {
        this.session = session;

        this.insertStmt = session.prepare(
                "INSERT INTO library.libraries_by_id (" +
                        "library_id, name, address, phone_number, email, website, " +
                        "open_on_weekends, opening_hours" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        );

        this.selectByIdStmt = session.prepare(
                "SELECT library_id, name, address, phone_number, email, website, " +
                        "open_on_weekends, opening_hours " +
                        "FROM library.libraries_by_id WHERE library_id = ?"
        );

        this.deleteStmt = session.prepare(
                "DELETE FROM library.libraries_by_id WHERE library_id = ?"
        );
        this.addressUdt = session.getMetadata()
                .getKeyspace("library")
                .flatMap(ks -> ks.getUserDefinedType("address"))
                .orElseThrow(() -> new IllegalStateException("UDT address not found"));

    }

    private UdtValue toUdt(Address a) {
        if (a == null) return null;

        return addressUdt.newValue()
                .setString("house_number", a.getHouseNumber())
                .setString("street", a.getStreet())
                .setString("city", a.getCity())
                .setString("state", a.getState())
                .setString("zipcode", a.getZipcode())
                .setString("country", a.getCountry());
    }

    private Address fromUdt(UdtValue v) {
        if (v == null) return null;

        return new Address(
                v.getString("house_number"),
                v.getString("street"),
                v.getString("city"),
                v.getString("state"),
                v.getString("zipcode"),
                v.getString("country")
        );
    }


    // CREATE
    public void insert(Library library) {
        session.execute(insertStmt.bind(
                library.getId(),
                library.getName(),
                toUdt(library.getAddress()),// UDT
                library.getPhoneNumber(),
                library.getEmail(),
                library.getWebsite(),
                library.isOpenOnWeekends(),
                library.getOpeningHours()
        ));
    }

    // READ
    public Library findById(String id) {
        Row row = session.execute(selectByIdStmt.bind(id)).one();
        if (row == null) {
            return null;
        }

        Library l = new Library();
        l.setId(row.getString("library_id"));
        l.setName(row.getString("name"));

        UdtValue udt = row.getUdtValue("address");
        l.setAddress(fromUdt(udt));

        l.setPhoneNumber(row.getString("phone_number"));
        l.setEmail(row.getString("email"));
        l.setWebsite(row.getString("website"));
        l.setOpenOnWeekends(row.getBoolean("open_on_weekends"));
        l.setOpeningHours(row.getString("opening_hours"));

        return l;
    }

    // UPDATE
    public void update(Library library) {
        insert(library);
    }

    // DELETE
    public void delete(String id) {
        session.execute(deleteStmt.bind(id));
    }
}