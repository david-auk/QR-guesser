package database.tables;

import database.core.Table;
import database.core.TimestampedTable;
import database.dao.PlaylistScanDAO;
import database.dao.TrackDAO;
import spotify.PlaylistScanTrack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlaylistScanTrackTable extends TimestampedTable<PlaylistScanTrack, String> {
    private final PlaylistScanDAO playlistScanDAO;
    private final TrackDAO trackDAO;

    public PlaylistScanTrackTable(PlaylistScanDAO playlistScanDAO, TrackDAO trackDAO) {
        super("playlist_scan_track", "id", String.class, "track_added_at",
                "INSERT INTO playlist_scan_track (id, playlist_scan_id, track_id, track_playlist_scan_index, track_added_at) VALUES (?, ?, ?, ?, ?)",
                "UPDATE playlist_scan_track SET playlist_scan_id = ?, track_id = ?, track_playlist_scan_index = ?, track_added_at = ? WHERE id = ?"
        );
        this.playlistScanDAO = playlistScanDAO;
        this.trackDAO = trackDAO;
    }

    @Override
    public void prepareAddStatement(PreparedStatement unPreparedStatement, PlaylistScanTrack playlistScanTrack) throws SQLException {
        unPreparedStatement.setString(1,playlistScanTrack.id());
        unPreparedStatement.setString(2,playlistScanTrack.playlistScan().getId());
        unPreparedStatement.setString(3,playlistScanTrack.track().id());
        unPreparedStatement.setInt(4,playlistScanTrack.index());
        unPreparedStatement.setTimestamp(5,playlistScanTrack.addedAt());
    }

    @Override
    public void prepareUpdateStatement(PreparedStatement unPreparedStatement, PlaylistScanTrack playlistScanTrack) throws SQLException {
        unPreparedStatement.setString(1, playlistScanTrack.playlistScan().getId());
        unPreparedStatement.setString(2, playlistScanTrack.track().id());
        unPreparedStatement.setInt(3, playlistScanTrack.index());
        unPreparedStatement.setTimestamp(4, playlistScanTrack.addedAt());
        unPreparedStatement.setString(5, playlistScanTrack.id());
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
