package dao;

import dao.Exceptions.DAOException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private final DBAccess access;
    public DBConnection(DBAccess access){
        this.access = access;
    }
    public static synchronized DBConnection getInstance() throws DAOException {
        if(instance== null){
            FileReader fileReader = FileReader.getInstance();
            DBAccess access = fileReader.getAccess("Database.properties");
            instance = new DBConnection(access);
        }
        return instance;
    }

    public static synchronized DBConnection getInstance(String properties) throws DAOException{
        if(instance == null) {
            FileReader fileReader = FileReader.getInstance();
            DBAccess access = fileReader.getAccess(properties);
            instance = new DBConnection(access);
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(access.getUrl(), access.getUser(), access.getPassword());
    }
}
