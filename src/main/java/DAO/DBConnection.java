package dao;

import dao.exceptions.DAOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static volatile DbConnection instance;
    private static DbAccess access;

    private DbConnection(DbAccess access) {
        this.access = access;
    }

    public static DbConnection getInstance() throws DAOException {
        DbConnection result = instance;
        if (result != null) {
            return result;
        }
        synchronized(DbConnection.class) {
            if (instance == null) {
                FileReader dataReader = FileReader.getInstance();
                DbAccess access = dataReader.getAccess("Database.properties");
                instance = new DbConnection(access);
            }
            return instance;
        }
    }

    public static DbConnection getInstance(String properties) throws DAOException {
        DbConnection result = instance;
        if (result != null) {
            return result;
        }
        synchronized(DbConnection.class) {
            if (instance == null) {
                FileReader dataReader = FileReader.getInstance();
                DbAccess access = dataReader.getAccess(properties);
                instance = new DbConnection(access);
            }
            return instance;
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(access.getUrl(), access.getUser(), access.getPassword());
    }
}
