package DAO;

import DAO.Exceptions.DAOException;
import DAO.Exceptions.MessagesConstants;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ScriptExecutor {
    public void executeScript(String fileName) throws DAOException {
        FileReader dataReader = FileReader.getInstance();
        execute(dataReader.getScript(fileName));
    }

    private void execute(String[] scripts) throws DAOException {
        DBConnection dbConnection = DBConnection.getInstance();
        try (Connection connection = dbConnection.getConnection();
             Statement statement = connection.createStatement()) {
            for (String line : scripts) {
                statement.addBatch(line);
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_EXECUTE_SCRIPTS, e);
        }
    }
}
