package database;

import java.util.List;

public interface GenericDAOInterface<T, K> extends AutoCloseable{
    void add(T entity);
    T create(T entity);
    void update(T entity);
    void delete(K primaryKey);
    T get(K primaryKey);
    T getLatest();
    List<T> getAll();
}
