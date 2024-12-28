package database;

import java.util.List;

public interface GenericDAOInterface<T> extends AutoCloseable{
    void add(T entity);
    T create(T entity);
    void update(T entity);
    void delete(String id);
    T get(String id);
    T getLatest();
    List<T> getAll();
}
