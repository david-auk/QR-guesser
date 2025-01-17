package database.tables;

import database.core.GenericDAO;
import spotify.Album;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AlbumDAO extends GenericDAO<Album, String> {

    protected AlbumDAO() {
        super("album", "id", String.class);
    }

    @Override
    public PreparedStatement prepareAddStatement(Album album) throws SQLException {
        String query = "INSERT INTO album (id, name, release_year) VALUES (?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, album.id());
        preparedStatement.setString(2, album.name());
        preparedStatement.setInt(3, album.releaseYear());
        return preparedStatement;
    }

    @Override
    public PreparedStatement prepareUpdateStatement(Album album) throws SQLException {
        String query = "UPDATE user SET name = ?, profile_picture_image_url = ? WHERE id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, album.name());
        preparedStatement.setInt(2, album.releaseYear());
        preparedStatement.setString(3, album.id());
        return preparedStatement;
    }

    @Override
    public Album buildFromTableWildcardQuery(ResultSet resultSet) throws SQLException {
        return new Album(
            resultSet.getString("id"),
            resultSet.getString("name"),
            resultSet.getInt("release_year")
        );
    }

    @Override
    public String getPrimaryKey(Album album) {
        return album.id();
    }
}
