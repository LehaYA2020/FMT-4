import dao.DbConnection;
import dao.exceptions.DAOException;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DbConnectionTest {
    @Test
    void getConnection() throws DAOException, SQLException {
        DbConnection dbConnection = DbConnection.getInstance();
        try (Connection connection = dbConnection.getConnection()) {
            assertTrue(connection.isValid(1));
            assertFalse(connection.isClosed());
        }
    }

    @Test
    void getTestConnection() throws DAOException, SQLException {
        DbConnection daoFactory = DbConnection.getInstance("database.properties");
        try (Connection connection = daoFactory.getConnection()) {
            assertTrue(connection.isValid(1));
            assertFalse(connection.isClosed());
        }
    }
}
