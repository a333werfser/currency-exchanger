package dao;

import java.util.List;

public interface Dao<T> {

    T get(int id);

    void add(T t);

    void update(T t, String... params);

    void delete(T t);

    List<T> getAll();
}
