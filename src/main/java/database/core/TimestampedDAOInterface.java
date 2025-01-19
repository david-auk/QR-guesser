package database.core;

import java.util.List;

public interface TimestampedDAOInterface<T, K> extends GenericDAOInterface<T, K> {
    List<T> getOrdered(Integer maxRecords, boolean ascending);
    List<T> getLatest(Integer maxRecords);
    List<T> getOldest(Integer maxRecords);
    T getLatest();
    T getOldest();
}
