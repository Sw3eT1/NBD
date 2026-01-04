package myLibrary.repositories;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import myLibrary.models.Address;
import myLibrary.models.Employee;

public class EmployeeRepository {

    private final CqlSession session;

    private final PreparedStatement insertStmt;
    private final PreparedStatement selectByIdStmt;
    private final PreparedStatement deleteStmt;

    public EmployeeRepository(CqlSession session) {
        this.session = session;

        this.insertStmt = session.prepare(
                "INSERT INTO library.employees_by_library (" +
                        "library_id, employee_id, name, surname, email, phone, " +
                        "address, position, salary, hire_date" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );

        this.selectByIdStmt = session.prepare(
                "SELECT library_id, employee_id, name, surname, email, phone, " +
                        "address, position, salary, hire_date " +
                        "FROM library.employees_by_library " +
                        "WHERE library_id = ? AND employee_id = ?"
        );

        this.deleteStmt = session.prepare(
                "DELETE FROM library.employees_by_library " +
                        "WHERE library_id = ? AND employee_id = ?"
        );
    }

    // CREATE
    public void insert(Employee employee) {
        session.execute(insertStmt.bind(
                employee.getLibraryId(),
                employee.getId(),
                employee.getName(),
                employee.getSurname(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getAddress(),      // UDT address
                employee.getPosition(),
                employee.getSalary(),
                employee.getHireDate()
        ));
    }

    // READ
    public Employee findById(String libraryId, String employeeId) {
        Row row = session.execute(selectByIdStmt.bind(libraryId, employeeId)).one();
        if (row == null) {
            return null;
        }

        Employee e = new Employee();
        e.setLibraryId(row.getString("library_id"));
        e.setId(row.getString("employee_id"));
        e.setName(row.getString("name"));
        e.setSurname(row.getString("surname"));
        e.setEmail(row.getString("email"));
        e.setPhone(row.getString("phone"));

        Address address = row.get("address", Address.class);
        e.setAddress(address);

        e.setPosition(row.getString("position"));
        e.setSalary(row.getDouble("salary"));
        e.setHireDate(row.getLocalDate("hire_date"));

        return e;
    }

    // UPDATE
    public void update(Employee employee) {
        insert(employee);
    }

    // DELETE
    public void delete(String libraryId, String employeeId) {
        session.execute(deleteStmt.bind(libraryId, employeeId));
    }
}