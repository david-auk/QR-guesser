package database;

import java.util.List;

public interface GenericDAOInterface<T> extends AutoCloseable{
    void add(T entity);
    T create(T entity);
    void update(T entity);
    void delete(int id);
    T get(int id);
    T getLatest();
    List<T> getAll();
}
