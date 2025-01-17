package database.core;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericDAO<T, K> implements GenericDAOInterface<T, K>, PreparedStatementInterface<T, K> {

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
    public boolean exists(T entity) {
        if (entity == null){
            return false;
        }
        K primaryKey = getPrimaryKey(entity);
        return primaryKey != null && get(primaryKey) != null;
    }

    @Override
    public void add(T entity) {
        try {
            PreparedStatement addStatement = prepareAddStatement(entity);
            addStatement.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public T create(T entity) {
        K primaryKey;
        try {
            PreparedStatement addStatement = prepareAddStatement(entity);
            addStatement.executeUpdate();

            // Get generated key (ID)
            try (ResultSet generatedKeys = addStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    primaryKey = generatedKeys.getObject(1, primaryKeyDataType);
                } else {
                    throw new SQLException("Creating threshold failed, no ID generated.");
                }
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }

        if (primaryKey == null) {
            throw new RuntimeException("Generated primary key could not be found.");
        }

        return get(primaryKey);
    }

    @Override
    public void update(T entity) {
        try {
            PreparedStatement updateStatement = prepareUpdateStatement(entity);
            updateStatement.executeUpdate();
            int rowsAffected = updateStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No rows affected");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T get(K primaryKey) {
        T entity = null;

        String query = "SELECT * FROM %s WHERE %s = ?";
        query = String.format(query, tableName, primaryKeyColumnName);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, primaryKey);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                 entity = buildFromTableWildcardQuery(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
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
            throw new RuntimeException(e);
        }
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
            throw new RuntimeException(e);
        }

        return entities;
    }

    @Override
    public void close() {
        Database.closeConnection(connection);
    }
}
