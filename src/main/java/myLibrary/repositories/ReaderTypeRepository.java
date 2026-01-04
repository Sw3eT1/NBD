package myLibrary.repositories;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import myLibrary.models.ReaderType;

public class ReaderTypeRepository {

    private final CqlSession session;

    private final PreparedStatement insertStmt;
    private final PreparedStatement selectByIdStmt;
    private final PreparedStatement deleteStmt;

    public ReaderTypeRepository(CqlSession session) {
        this.session = session;

        this.insertStmt = session.prepare(
                "INSERT INTO library.reader_types (" +
                        "reader_type_id, name, max_books" +
                        ") VALUES (?, ?, ?)"
        );

        this.selectByIdStmt = session.prepare(
                "SELECT reader_type_id, name, max_books " +
                        "FROM library.reader_types WHERE reader_type_id = ?"
        );

        this.deleteStmt = session.prepare(
                "DELETE FROM library.reader_types WHERE reader_type_id = ?"
        );
    }

    // CREATE
    public void insert(ReaderType type) {
        session.execute(insertStmt.bind(
                type.getId(),
                type.getName(),
                type.getMaxBooks()
        ));
    }

    // READ
    public ReaderType findById(String id) {
        Row row = session.execute(selectByIdStmt.bind(id)).one();
        if (row == null) {
            return null;
        }

        // Tworzymy "bazowy" typ – w razie potrzeby możesz tu rozróżniać po name
        ReaderType type = new ReaderType(row.getString("name"), row.getInt("max_books")) {};
        type.setId(row.getString("reader_type_id"));

        return type;
    }

    // UPDATE
    public void update(ReaderType type) {
        insert(type);
    }

    // DELETE
    public void delete(String id) {
        session.execute(deleteStmt.bind(id));
    }
}