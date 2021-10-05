import dao.DBConnection;
import dao.exceptions.DAOException;
import dao.ScriptExecutor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScriptExecutorTest {
    private static DBConnection dbConnection;
    private final ScriptExecutor scriptExecutor = new ScriptExecutor();

    @BeforeAll
    public static void prepare() throws DAOException {
        dbConnection = DBConnection.getInstance("TestDatabaseH2.properties");
    }

    @Test
    public void shouldReturnTrueIfStudentsTableWasCreated() throws DAOException {
        scriptExecutor.executeScript("CreateTables.sql");
        assertTrue(checkTableForExistence("STUDENTS"));
    }

    @Test
    public void shouldReturnTrueIfGroupsTableWasCreated() throws DAOException {
        scriptExecutor.executeScript("CreateTables.sql");
        assertTrue(checkTableForExistence("GROUPS"));
    }

    @Test
    public void shouldReturnTrueIfCoursesTableWasCreated() throws DAOException {
        scriptExecutor.executeScript("CreateTables.sql");
        assertTrue(checkTableForExistence("COURSES"));
    }

    @Test
    public void shouldReturnTrueIfStudentsGroupsTableWasCreated() throws DAOException {
        scriptExecutor.executeScript("CreateTables.sql");
        assertTrue(checkTableForExistence("STUDENTS_GROUPS"));
    }

    @Test
    public void shouldReturnTrueIfStudentsCoursesTableWasCreated() throws DAOException {
        scriptExecutor.executeScript("CreateTables.sql");
        assertTrue(checkTableForExistence("STUDENTS_COURSES"));
    }

    private boolean checkTableForExistence(String tableName) throws DAOException {
        try (Connection connection = dbConnection.getConnection();
             ResultSet resultSet = connection.getMetaData().getTables(
                     null, null, tableName, null)) {
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new DAOException("Cannot check table for existence.", e);
        }
        return false;
    }
}
