package database.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class GenericTimestampedDAO<T, K> extends GenericDAO<T, K> {

    protected final String timestampColumnName;

    protected GenericTimestampedDAO(String tableName, String primaryKeyColumnName, Class<K> primaryKeyDataType, String timestampColumnName, String addQuery, String updateQuery) {
        super(tableName, primaryKeyColumnName, primaryKeyDataType, addQuery, updateQuery);
        this.timestampColumnName = timestampColumnName;
    }


    public List<T> getOrdered(Integer maxRecords, boolean ascending){
        String query = "SELECT %s FROM %s ORDER BY %s %s LIMIT ?";
        query = String.format(query, primaryKeyColumnName, tableName, timestampColumnName, ascending ? "DESC" : "ASC");
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
