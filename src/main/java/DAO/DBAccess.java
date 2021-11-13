package dao;

public class DbAccess {
    private final String url;
    private final String user;
    private final String password;

    public DbAccess(String url, String user, String password) {
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
        if (o == null || getClass() != o.getClass()) return false;

        DbAccess dbAccess = (DbAccess) o;

        if (url != null ? !url.equals(dbAccess.url) : dbAccess.url != null) return false;
        if (user != null ? !user.equals(dbAccess.user) : dbAccess.user != null) return false;
        return password != null ? password.equals(dbAccess.password) : dbAccess.password == null;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
