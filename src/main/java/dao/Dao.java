package dao;

import java.sql.SQLException;
import java.util.List;

public interface Dao<T> {

    T get(int id);

    void add(T t);

    void update(T t, String field) throws SQLException;

    List<T> getAll() throws SQLException;
}
