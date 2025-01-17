package database.tables;

import database.core.GenericDAO;
import spotify.AccessToken;
import spotify.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccessTokenDAO extends GenericDAO<AccessToken, String> {
    private final UserDAO userDAO;

    public AccessTokenDAO(UserDAO userDAO) {
        super("access_token", "id", String.class);

        this.userDAO = userDAO;
    }

    // Handle user does not exist yet
    @Override
    public void add(AccessToken accessToken) {
        User user = accessToken.getUser();
        if (!userDAO.exists(user)) {
            userDAO.add(user);
        }
        super.add(accessToken);
    }

    @Override
    public PreparedStatement prepareAddStatement(AccessToken accessToken) throws SQLException {
        String query = "INSERT INTO access_token (id, access_token, refresh_token, user_id, created_at, expires_at) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);

        // Set the values
        preparedStatement.setString(1, accessToken.getId());
        preparedStatement.setString(2, accessToken.getAccessToken());
        preparedStatement.setString(3, accessToken.getRefreshToken());
        preparedStatement.setString(4, accessToken.getUser().id());
        preparedStatement.setTimestamp(5, accessToken.getCreatedAt());
        preparedStatement.setTimestamp(6, accessToken.getExpiresAt());
        return preparedStatement;
    }

    @Override
    public PreparedStatement prepareUpdateStatement(AccessToken accessToken) throws SQLException {
        String query = "UPDATE access_token SET access_token = ?, refresh_token = ?, expires_at = ? WHERE id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, accessToken.getAccessToken());
        preparedStatement.setString(2, accessToken.getRefreshToken());
        preparedStatement.setTimestamp(3, accessToken.getExpiresAt());
        preparedStatement.setString(4, accessToken.getId());

        return preparedStatement;
    }

    @Override
    public AccessToken buildFromTableWildcardQuery(ResultSet resultSet) throws SQLException {
        return new AccessToken(
            resultSet.getString("id"),
            resultSet.getString("access_token"),
            resultSet.getString("refresh_token"),
            userDAO.get(resultSet.getString("user_id")),
            resultSet.getTimestamp("created_at"),
            resultSet.getTimestamp("expires_at")
        );
    }

    @Override
    public String getPrimaryKey(AccessToken accessToken) {
        return accessToken.getId();
    }
}
