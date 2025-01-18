package database.core;

import database.tables.Table;
import database.tables.TableInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericDAO<T, K> implements GenericDAOInterface<T, K> {

    protected final Connection connection;
    private final Table<T, K> table;

    // Constructor to enforce initialization
    protected GenericDAO(Table<T, K> table) {
        this.table = table;
        this.connection = Database.getConnection();
    }

    @Override
    public boolean exists(T entity) {
        if (entity == null){
            return false;
        }
        K primaryKey = table.getPrimaryKey(entity);
        return primaryKey != null && get(primaryKey) != null;
    }

    @Override
    public void add(T entity) {
        try {
            PreparedStatement addStatement = connection.prepareStatement(table.getAddQuery());
            table.prepareAddStatement(addStatement, entity);
            addStatement.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public T create(T entity) {
        K primaryKey;
        try {
            PreparedStatement addStatement = connection.prepareStatement(table.getAddQuery(), PreparedStatement.RETURN_GENERATED_KEYS);
            table.prepareAddStatement(addStatement, entity);
            addStatement.executeUpdate();

            // Get generated key
            try (ResultSet generatedKeys = addStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    primaryKey = generatedKeys.getObject(1, table.getPrimaryKeyDataType());
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
            PreparedStatement updateStatement = connection.prepareStatement(table.getUpdateQuery());
            table.prepareUpdateStatement(updateStatement, entity);
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
        query = String.format(query, table.getTableName(), table.getPrimaryKeyColumnName());

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, primaryKey);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                 entity = table.buildFromTableWildcardQuery(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

    @Override
    public void delete(K primaryKey) {
        String query = "DELETE FROM %s WHERE %s = ?";
        query = String.format(query, table.getTableName(), table.getPrimaryKeyColumnName());

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
        query = String.format(query, table.getPrimaryKeyColumnName(), table.getTableName());
        List<T> entities;
        try {
            Statement statement = connection.createStatement();
            entities = getEntities(statement.executeQuery(query));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return entities;
    }

    protected List<T> getEntities(ResultSet resultSet) throws SQLException {
        List<T> entities = new ArrayList<>();
        while (resultSet.next()) {
            T entity = get(resultSet.getObject(table.getPrimaryKeyColumnName(), table.getPrimaryKeyDataType()));
            entities.add(entity);
        }
        return entities;
    }

    @Override
    public void close() {
        Database.closeConnection(connection);
    }
}
