package database;

import spotify.AccessToken;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccessTokenDAO extends GenericDAO<AccessToken, String> {
    private final UserDAO userDAO;

    public AccessTokenDAO(UserDAO userDAO) {
        super("access_token", "id", String.class);

        this.userDAO = userDAO;
    }

    @Override
    public void add(AccessToken accessToken) {

        if (get(accessToken.getId()) == null) {
            userDAO.add(accessToken.getUser());

            String query = "INSERT INTO access_token (id, access_token, refresh_token, user_id, created_at, expires_at) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, accessToken.getId());
                preparedStatement.setString(2, accessToken.getAccessToken());
                preparedStatement.setString(3, accessToken.getRefreshToken());
                preparedStatement.setString(4, accessToken.getUser().id());
                preparedStatement.setTimestamp(5, accessToken.getCreatedAt());
                preparedStatement.setTimestamp(6, accessToken.getExpiresAt());

                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public void update(AccessToken accessToken) {
        if (accessToken.getId() == null){
            throw new IllegalArgumentException("AccessToken has no ID");
        }

        String query = "UPDATE access_token SET access_token = ?, refresh_token = ?, expires_at = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, accessToken.getAccessToken());
            preparedStatement.setString(2, accessToken.getRefreshToken());
            preparedStatement.setTimestamp(3, accessToken.getExpiresAt());
            preparedStatement.setString(4, accessToken.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new IllegalArgumentException("No AccessToken found with ID: " + accessToken.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public AccessToken get(String id) {
        String query = "SELECT * FROM access_token WHERE id = ?";
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
                    resultSet.getTimestamp("created_at"),
                    resultSet.getTimestamp("expires_at")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        return accessToken;
    }
}
