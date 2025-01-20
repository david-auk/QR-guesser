package database.tables;

import database.core.Table;
import database.dao.AlbumDAO;
import database.dao.ArtistDAO;
import spotify.Track;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrackTable extends Table<Track, String> {
    private final AlbumDAO albumDAO;
    private final ArtistDAO artistDAO;

    public TrackTable(AlbumDAO albumDAO, ArtistDAO artistDAO) {
        super("track", "id", String.class,
                "INSERT INTO track (id, name, album_id) VALUES (?, ?, ?)",
                "UPDATE track SET name = ?, album_id = ? WHERE id = ?"
        );
        this.albumDAO = albumDAO;
        this.artistDAO = artistDAO;
    }

    @Override
    public void prepareAddStatement(PreparedStatement unPreparedStatement, Track track) throws SQLException {
        unPreparedStatement.setString(1, track.id());
        unPreparedStatement.setString(2, track.name());
        unPreparedStatement.setString(3, track.album().id());
    }

    @Override
    public void prepareUpdateStatement(PreparedStatement unPreparedStatement, Track track) throws SQLException {
        unPreparedStatement.setString(1, track.id());
        unPreparedStatement.setString(2, track.name());
        unPreparedStatement.setString(3, track.album().id());
    }

    @Override
    public Track buildFromTableWildcardQuery(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("id");

        return new Track(
                id,
                resultSet.getString("name"),
                albumDAO.get(resultSet.getString("album_id")),
                artistDAO.getArtistsByTrackId(id)
        );
    }

    @Override
    public String getPrimaryKey(Track track) {
        return track.id();
    }
}
