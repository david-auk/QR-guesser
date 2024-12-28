package database;

import spotify.User;

import java.sql.*;
import java.util.ArrayList;

public class UserDAO extends GenericDAO<User> {

    public UserDAO() {
        super("user");
    }

    @Override
    public void add(User user) {

        // Check if user already exists
        if (get(user.id()) == null) {
            String query = "INSERT INTO user (id, name, profile_picture_image_url) VALUES (?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, user.id());
                preparedStatement.setString(2, user.name());
                preparedStatement.setString(3, user.profilePictureImageUrl());
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(User user) {

        if (user.id() == null){
            throw new IllegalArgumentException("User has no ID");
        }

        String query = "UPDATE user SET name = ?, profile_picture_image_url = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, user.name());
            preparedStatement.setString(2, user.profilePictureImageUrl());
            preparedStatement.setString(3, user.id());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new IllegalArgumentException("No user found with ID: " + user.id());
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public User get(String id) {
        String query = "SELECT * FROM user WHERE id = ?";
        User user = null;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User(
                        id,
                        resultSet.getString("name"),
                        resultSet.getString("profile_picture_image_url")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return user;
    }

}
