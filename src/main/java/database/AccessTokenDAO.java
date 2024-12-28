package database;

import spotify.AccessToken;
import spotify.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccessTokenDAO extends GenericDAO<AccessToken> {
    private final UserDAO userDAO;

    public AccessTokenDAO(UserDAO userDAO) {
        super("access_token");

        this.userDAO = userDAO;
    }

    @Override
    public void add(AccessToken accessToken) {

        // TODO add already exists check

        userDAO.add(accessToken.getUser());

        String query = "INSERT INTO access_token (id, access_token, refresh_token, user_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, accessToken.getId());
            preparedStatement.setString(2, accessToken.getAccessToken());
            preparedStatement.setString(3, accessToken.getRefreshToken());
            preparedStatement.setString(4, accessToken.getUser().id());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void update(AccessToken accessToken) {
        if (accessToken.getId() == null){
            throw new IllegalArgumentException("AccessToken has no ID");
        }

        String query = "UPDATE access_token SET access_token = ?, refresh_token = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, accessToken.getAccessToken());
            preparedStatement.setString(2, accessToken.getRefreshToken());
            preparedStatement.setString(3, accessToken.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new IllegalArgumentException("No AccessToken found with ID: " + accessToken.getId());
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public AccessToken get(String id) {
        String query = "SELECT * FROM user WHERE id = ?";
        AccessToken accessToken = null;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                accessToken = new AccessToken(
                    id,
                    resultSet.getString("access_token"),
                    resultSet.getString("refresh_token"),
                    userDAO.get(resultSet.getString("user_id")),
                    resultSet.getTimestamp("timestamp")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return accessToken;
    }
}
