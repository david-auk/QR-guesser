package database.tables;

import database.core.Table;
import spotify.Album;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AlbumTable extends Table<Album, String> {

    public AlbumTable() {
        super("album", "id", String.class,
                "INSERT INTO album (id, name, release_year) VALUES (?, ?, ?)",
                "UPDATE album SET name = ?, release_year = ? WHERE id = ?"
        );
    }

    @Override
    public void prepareAddStatement(PreparedStatement unPreparedStatement, Album album) throws SQLException {
        unPreparedStatement.setString(1, album.id());
        unPreparedStatement.setString(2, album.name());
        unPreparedStatement.setInt(3, album.releaseYear());
    }

    @Override
    public void prepareUpdateStatement(PreparedStatement unPreparedStatement, Album album) throws SQLException {
        unPreparedStatement.setString(1, album.name());
        unPreparedStatement.setInt(2, album.releaseYear());
        unPreparedStatement.setString(3, album.id());
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
