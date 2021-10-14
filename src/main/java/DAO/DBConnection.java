package dao;

import dao.exceptions.DAOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static DbConnection instance;
    private static DbAccess access;

    public DbConnection() {
        getInstance();
    }

    public DbConnection(String properties) {
        getInstance(properties);
    }

    private static synchronized DbConnection getInstance() throws DAOException {
        if (instance == null) {
            FileReader fileReader = FileReader.getInstance();
            access = fileReader.getAccess("database.properties");
            instance = new DbConnection();
        }
        return instance;
    }

    private static synchronized DbConnection getInstance(String properties) throws DAOException {
        if (instance == null) {
            FileReader fileReader = FileReader.getInstance();
            access = fileReader.getAccess(properties);
            instance = new DbConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(access.getUrl(), access.getUser(), access.getPassword());
    }
}
