package database.core;

import database.tables.TimestampedTable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public abstract class GenericTimestampedDAO<T, K> extends GenericDAO<T, K> {

    private final TimestampedTable<T, K> table;

    protected GenericTimestampedDAO(TimestampedTable<T, K> table) {
        super(table);
        this.table = table;
    }

    public List<T> getOrdered(Integer maxRecords, boolean ascending){
        String query = "SELECT %s FROM %s ORDER BY %s %s LIMIT ?";
        query = String.format(query, table.getPrimaryKeyColumnName(), table.getTableName(), table.getTimestampColumnName(), ascending ? "DESC" : "ASC");
        List<T> entities = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setInt(1, maxRecords);
            entities = getEntities(preparedStatement.executeQuery());
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return entities;
    }

    public List<T> getLatest(Integer maxRecords){
        return getOrdered(maxRecords, false);
    }

    public List<T> getOldest(Integer maxRecords){
        return getOrdered(maxRecords, true);
    }

    public T getLatest(){
        return getLatest(1).get(0);
    }

    public T getOldest(){
        return getOldest(1).get(0);
    }
}
