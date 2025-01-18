package database.tables;

import spotify.Artist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArtistTable extends Table<Artist, String> {
    public ArtistTable() {
        super("artist", "id", String.class,
                "INSERT INTO artist (id, name) VALUES (?, ?)",
                "UPDATE artist SET name = ? WHERE id = ?"
        );
    }

    @Override
    public void prepareAddStatement(PreparedStatement unPreparedStatement,Artist artist) throws SQLException {
        unPreparedStatement.setString(1, artist.id());
        unPreparedStatement.setString(2, artist.name());
    }

    @Override
    public void prepareUpdateStatement(PreparedStatement unPreparedStatement, Artist artist) throws SQLException {
        unPreparedStatement.setString(1, artist.name());
        unPreparedStatement.setString(2, artist.id());
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
