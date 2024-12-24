package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericDAO<T> implements GenericDAOInterface<T> {

    protected final Connection connection;
    protected final String tableName;

    // Constructor to enforce initialization
    protected GenericDAO(String tableName, String username, String password) {
        if (tableName == null || username == null || password == null) {
            throw new IllegalArgumentException("tableName, username or password must not be null.");
        }
        this.connection = getConnection(username, password);
        this.tableName = tableName;
    }

    static private Connection getConnection(String username, String password) {
        return Database.getConnection(username, password);
    }

    @Override
    public T create(T entity) {
        add(entity);
        return getLatest();
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM %s WHERE id = ?";
        query = String.format(query, tableName);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public T getLatest() {
        String query = "SELECT ID FROM %s ORDER BY ID DESC LIMIT 1";
        query = String.format(query, tableName);

        T entity = null;

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                entity = get(resultSet.getInt("ID"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return entity;
    }

    @Override
    public List<T> getAll() {
        String query = "SELECT ID FROM %s";
        query = String.format(query, tableName);
        List<T> entities = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                T entity = get(resultSet.getInt("ID"));
                entities.add(entity);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return entities;
    }

    @Override
    public void close() {
        Database.closeConnection(connection);
    }
}
