package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericDAO<T> implements GenericDAOInterface<T> {

    protected final Connection connection;
    protected final String tableName;

    // Constructor to enforce initialization
    protected GenericDAO(String tableName) {
        this.connection = Database.getConnection();
        this.tableName = tableName;
    }

    @Override
    public T create(T entity) {
        add(entity);
        return getLatest();
    }

    @Override
    public void delete(String id) {
        String query = "DELETE FROM %s WHERE id = ?";
        query = String.format(query, tableName);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, id);
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
                entity = get(resultSet.getString("id"));
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
                T entity = get(resultSet.getString("ID"));
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
