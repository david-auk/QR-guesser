package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericDAO<T, K> implements GenericDAOInterface<T, K> {

    protected final Connection connection;
    private final String tableName;
    private final String primaryKeyColumnName;
    private final Class<K> primaryKeyDataType;

    // Constructor to enforce initialization
    protected GenericDAO(String tableName, String primaryKeyColumnName, Class<K> primaryKeyDataType) {
        this.connection = Database.getConnection();
        this.tableName = tableName;
        this.primaryKeyColumnName = primaryKeyColumnName;
        this.primaryKeyDataType = primaryKeyDataType;
    }

    @Override
    public T create(T entity) {
        add(entity);
        return getLatest();
    }

    @Override
    public void delete(K primaryKey) {
        String query = "DELETE FROM %s WHERE %s = ?";
        query = String.format(query, tableName, primaryKeyColumnName);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setObject(1, primaryKey);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public T getLatest() {
        String query = "SELECT %s FROM %s ORDER BY %s DESC LIMIT 1";
        query = String.format(query, primaryKeyColumnName, tableName, primaryKeyColumnName);

        T entity = null;

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                entity = get(resultSet.getObject(primaryKeyColumnName, primaryKeyDataType));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        return entity;
    }

    @Override
    public List<T> getAll() {
        String query = "SELECT %s FROM %s";
        query = String.format(query, primaryKeyColumnName, tableName);
        List<T> entities = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                T entity = get(resultSet.getObject(primaryKeyColumnName, primaryKeyDataType));
                entities.add(entity);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        return entities;
    }

    @Override
    public void close() {
        Database.closeConnection(connection);
    }
}
