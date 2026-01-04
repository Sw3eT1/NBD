package myLibrary.repositories;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import myLibrary.models.Address;
import myLibrary.models.Reader;

public class ReaderRepository {

    private final CqlSession session;

    private final PreparedStatement insertStmt;
    private final PreparedStatement selectByIdStmt;
    private final PreparedStatement deleteStmt;

    public ReaderRepository(CqlSession session) {
        this.session = session;

        this.insertStmt = session.prepare(
                "INSERT INTO library.readers_by_library (" +
                        "library_id, reader_id, name, surname, email, phone, " +
                        "address, card_number, reader_type_id, active_rentals" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );

        this.selectByIdStmt = session.prepare(
                "SELECT library_id, reader_id, name, surname, email, phone, " +
                        "address, card_number, reader_type_id, active_rentals " +
                        "FROM library.readers_by_library " +
                        "WHERE library_id = ? AND reader_id = ?"
        );

        this.deleteStmt = session.prepare(
                "DELETE FROM library.readers_by_library " +
                        "WHERE library_id = ? AND reader_id = ?"
        );
    }

    // CREATE
    public void insert(Reader reader) {
        session.execute(insertStmt.bind(
                reader.getLibraryId(),
                reader.getId(),
                reader.getName(),
                reader.getSurname(),
                reader.getEmail(),
                reader.getPhone(),
                reader.getAddress(),          // UDT Address
                reader.getCardNumber(),
                reader.getReaderTypeId(),
                reader.getActiveRentals()
        ));
    }

    // READ
    public Reader findById(String libraryId, String readerId) {
        Row row = session.execute(selectByIdStmt.bind(libraryId, readerId)).one();
        if (row == null) {
            return null;
        }

        Reader r = new Reader();
        r.setLibraryId(row.getString("library_id"));
        r.setId(row.getString("reader_id"));
        r.setName(row.getString("name"));
        r.setSurname(row.getString("surname"));
        r.setEmail(row.getString("email"));
        r.setPhone(row.getString("phone"));

        Address address = row.get("address", Address.class);
        r.setAddress(address);

        r.setCardNumber(row.getString("card_number"));
        r.setReaderTypeId(row.getString("reader_type_id"));
        r.setActiveRentals(row.getInt("active_rentals"));

        return r;
    }

    // UPDATE = upsert
    public void update(Reader reader) {
        insert(reader);
    }

    // DELETE
    public void delete(String libraryId, String readerId) {
        session.execute(deleteStmt.bind(libraryId, readerId));
    }
}