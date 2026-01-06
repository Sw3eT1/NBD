package myLibrary.repositories;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.mapper.annotations.*;

@Mapper
public interface LibraryMapper {

    @DaoFactory
    BookDao bookDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    LibraryDao libraryDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    ReaderTypeDao readerTypeDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    ReaderDao readerDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    EmployeeDao employeeDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    BookCopyDao bookCopyDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    RentalDao rentalDao(@DaoKeyspace CqlIdentifier keyspace);
}