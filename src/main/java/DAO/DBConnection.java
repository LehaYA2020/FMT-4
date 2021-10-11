package dao;

import dao.exceptions.DAOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private static DBAccess access;

    public static synchronized DBConnection getInstance() throws DAOException {
        if (instance == null) {
            FileReader fileReader = FileReader.getInstance();
            access = fileReader.getAccess("database.properties");
            instance = new DBConnection();
        }
        return instance;
    }

    public static synchronized DBConnection getInstance(String properties) throws DAOException {
        if (instance == null) {
            FileReader fileReader = FileReader.getInstance();
            access = fileReader.getAccess(properties);
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(access.getUrl(), access.getUser(), access.getPassword());
    }
}
