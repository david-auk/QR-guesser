package database.tables;

import database.core.GenericDAO;
import spotify.User;

import java.sql.*;

public class UserDAO extends GenericDAO<User, String> {

    public UserDAO() {
        super("user", "id", String.class,
                "INSERT INTO user (id, name, profile_picture_image_url) VALUES (?, ?, ?)",
                "UPDATE user SET name = ?, profile_picture_image_url = ? WHERE id = ?"
        );
    }

    @Override
    public void prepareAddStatement(PreparedStatement unPreparedStatement, User user) throws SQLException {
        unPreparedStatement.setString(1, user.id());
        unPreparedStatement.setString(2, user.name());
        unPreparedStatement.setString(3, user.profilePictureImageUrl());
    }

    @Override
    public void prepareUpdateStatement(PreparedStatement unPreparedStatement, User user) throws SQLException {
        unPreparedStatement.setString(1, user.name());
        unPreparedStatement.setString(2, user.profilePictureImageUrl());
        unPreparedStatement.setString(3, user.id());
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