package DAO;

import java.util.Objects;

public class DBAccess {
    private final String url;
    private final String user;
    private final String password;

    public DBAccess(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DBAccess)) return false;

        DBAccess that = (DBAccess) o;

        if (!url.equals(that.url)) return false;
        if (!user.equals(that.user)) return false;
        return password.equals(that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, user, password);
    }
}
