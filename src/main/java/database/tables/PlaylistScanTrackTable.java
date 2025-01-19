package database.tables;

import database.core.Table;
import database.dao.PlaylistScanDAO;
import database.dao.TrackDAO;
import spotify.PlaylistScanTrack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlaylistScanTrackTable extends Table<PlaylistScanTrack, String> {
    private final PlaylistScanDAO playlistScanDAO;
    private final TrackDAO trackDAO;

    public PlaylistScanTrackTable(PlaylistScanDAO playlistScanDAO, TrackDAO trackDAO) {
        super("playlist_scan_track", "id", String.class,
                "addQuery",
                "updateQuery"
        );
        this.playlistScanDAO = playlistScanDAO;
        this.trackDAO = trackDAO;
    }

    @Override
    public void prepareAddStatement(PreparedStatement unPreparedStatement, PlaylistScanTrack playlistScanTrack) throws SQLException {

    }

    @Override
    public void prepareUpdateStatement(PreparedStatement unPreparedStatement, PlaylistScanTrack playlistScanTrack) throws SQLException {

    }

    @Override
    public PlaylistScanTrack buildFromTableWildcardQuery(ResultSet resultSet) throws SQLException {
        return new PlaylistScanTrack(
                resultSet.getString("id"),
                playlistScanDAO.get(resultSet.getString("playlist_scan_id")),
                trackDAO.get(resultSet.getString("track_id")),
                resultSet.getInt("track_playlist_scan_index"),
                resultSet.getTimestamp("track_added_at")
        );
    }

    @Override
    public String getPrimaryKey(PlaylistScanTrack playlistScanTrack) {
        return playlistScanTrack.id();
    }
}
