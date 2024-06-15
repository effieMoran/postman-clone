package dao;

import java.util.List;

public interface Dao<T> {
    T get(String id);
    List<T> getAll();
    T save(T t);
    void update(T t);
    void delete(T t);

    void delete(String id);
}
