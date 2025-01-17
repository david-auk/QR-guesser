package database.tables;

import database.core.GenericDAO;
import spotify.Artist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArtistDAO extends GenericDAO<Artist, String> {
    protected ArtistDAO() {
        super("artist", "id", String.class);
    }

    @Override
    public PreparedStatement prepareAddStatement(Artist artist) throws SQLException {
        String query = "INSERT INTO artist (id, name) VALUES (?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, artist.id());
        preparedStatement.setString(2, artist.name());
        return preparedStatement;
    }

    @Override
    public PreparedStatement prepareUpdateStatement(Artist artist) throws SQLException {
        String query = "UPDATE artist SET name = ? WHERE id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, artist.name());
        preparedStatement.setString(2, artist.id());
        return preparedStatement;
    }

    @Override
    public Artist buildFromTableWildcardQuery(ResultSet resultSet) throws SQLException {
        return new Artist(
            resultSet.getString("id"),
            resultSet.getString("name")
        );
    }

    @Override
    public String getPrimaryKey(Artist artist) {
        return artist.id();
    }
}
