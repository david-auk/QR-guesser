package database.tables;

import spotify.Playlist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlaylistTable extends Table<Playlist, String> {

    public PlaylistTable() {
        super("playlist", "id", String.class,
                "INSERT INTO playlist (id, title, cover_image_url) VALUES (?, ?, ?)",
                "UPDATE playlist SET title = ?, cover_image_url = ? WHERE id = ?"
        );
    }

    @Override
    public void prepareAddStatement(PreparedStatement unPreparedStatement, Playlist playlist) throws SQLException {
        unPreparedStatement.setString(1, playlist.id());
        unPreparedStatement.setString(2, playlist.title());
        unPreparedStatement.setString(3, playlist.coverImageUrl());

    }

    @Override
    public void prepareUpdateStatement(PreparedStatement unPreparedStatement, Playlist playlist) throws SQLException {
        unPreparedStatement.setString(1, playlist.title());
        unPreparedStatement.setString(2, playlist.coverImageUrl());
        unPreparedStatement.setString(3, playlist.id());
    }

    @Override
    public Playlist buildFromTableWildcardQuery(ResultSet resultSet) throws SQLException {
        return new Playlist(
                resultSet.getString("id"),
                resultSet.getString("title"),
                resultSet.getString("cover_image_url")
        );
    }

    @Override
    public String getPrimaryKey(Playlist playlist) {
        return playlist.id();
    }
}
