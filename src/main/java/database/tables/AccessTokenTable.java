package database.tables;

import database.core.TimestampedTable;
import database.dao.UserDAO;
import spotify.AccessToken;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccessTokenTable extends TimestampedTable<AccessToken, String> {

    private final UserDAO userDAO;

    public AccessTokenTable(UserDAO userDAO) {
        super("access_token", "id", String.class, "created_at",
                "INSERT INTO access_token (id, access_token, refresh_token, user_id, created_at, expires_at) VALUES (?, ?, ?, ?, ?, ?)",
        "UPDATE access_token SET access_token = ?, refresh_token = ?, expires_at = ? WHERE id = ?");
        this.userDAO = userDAO;
    }

    @Override
    public void prepareAddStatement(PreparedStatement unPreparedStatement, AccessToken accessToken) throws SQLException {
        unPreparedStatement.setString(1, accessToken.getId());
        unPreparedStatement.setString(2, accessToken.getAccessToken());
        unPreparedStatement.setString(3, accessToken.getRefreshToken());
        unPreparedStatement.setString(4, accessToken.getUser().id());
        unPreparedStatement.setTimestamp(5, accessToken.getCreatedAt());
        unPreparedStatement.setTimestamp(6, accessToken.getExpiresAt());
    }

    @Override
    public void prepareUpdateStatement(PreparedStatement unPreparedStatement, AccessToken accessToken) throws SQLException {
        unPreparedStatement.setString(1, accessToken.getAccessToken());
        unPreparedStatement.setString(2, accessToken.getRefreshToken());
        unPreparedStatement.setTimestamp(3, accessToken.getExpiresAt());
        unPreparedStatement.setString(4, accessToken.getId());
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
