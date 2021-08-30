import dao.DBConnection;
import dao.Exceptions.DAOException;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DBConnectionTest {
    @Test
    void getConnection() throws DAOException, SQLException {
        DBConnection dbConnection = DBConnection.getInstance();
        try (Connection connection = dbConnection.getConnection()) {
            assertTrue(connection.isValid(1));
            assertFalse(connection.isClosed());
        }
    }

    @Test
    void getTestConnection() throws DAOException, SQLException {
        DBConnection daoFactory = DBConnection.getInstance("TestDatabaseH2.properties");
        try (Connection connection = daoFactory.getConnection()) {
            assertTrue(connection.isValid(1));
            assertFalse(connection.isClosed());
        }
    }
}
