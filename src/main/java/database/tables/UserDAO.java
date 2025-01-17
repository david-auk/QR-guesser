package database.tables;

import database.core.GenericDAO;
import spotify.User;

import java.sql.*;

public class UserDAO extends GenericDAO<User, String> {

    public UserDAO() {
        super("user", "id", String.class);
    }

    @Override
    public PreparedStatement prepareAddStatement(User user) throws SQLException {
        String query = "INSERT INTO user (id, name, profile_picture_image_url) VALUES (?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, user.id());
        preparedStatement.setString(2, user.name());
        preparedStatement.setString(3, user.profilePictureImageUrl());
        return preparedStatement;
    }

    @Override
    public PreparedStatement prepareUpdateStatement(User user) throws SQLException {
        String query = "UPDATE user SET name = ?, profile_picture_image_url = ? WHERE id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, user.name());
        preparedStatement.setString(2, user.profilePictureImageUrl());
        preparedStatement.setString(3, user.id());

        return preparedStatement;
    }

    @Override
    public User buildFromTableWildcardQuery(ResultSet resultSet) throws SQLException {
        return new User(
            resultSet.getString("id"),
            resultSet.getString("name"),
            resultSet.getString("profile_picture_image_url")
        );
    }

    @Override
    public String getPrimaryKey(User user) {
        return user.id();
    }
}