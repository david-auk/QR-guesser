package database.tables;

import database.core.TimestampedTable;
import database.dao.PlaylistDAO;
import database.dao.PlaylistScanDAO;
import database.dao.PlaylistScanTrackDAO;
import database.dao.UserDAO;
import spotify.PlaylistScan;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlaylistScanTable extends TimestampedTable<PlaylistScan, String> {

    private final PlaylistScanDAO playlistScanDAO;
    private final PlaylistDAO playlistDAO;
    private final PlaylistScanTrackDAO playlistScanTrackDAO;
    private final UserDAO userDAO;

    public PlaylistScanTable(PlaylistScanDAO playlistScanDAO, PlaylistDAO playlistDAO, PlaylistScanTrackDAO playlistScanTrackDAO, UserDAO userDAO) {
        super("playlist_scan", "id", String.class, "timestamp",
                "INSERT INTO playlist_scan (id, playlist_id, requested_by_user_id, export_completed, extends_playlist_scan, timestamp) VALUES (?, ?, ?, ?, ?, ?)",
                "UPDATE playlist_scan SET playlist_id = ?, requested_by_user_id = ?, export_completed = ?, extends_playlist_scan = ? WHERE id = ?"
        );
        this.playlistScanDAO = playlistScanDAO;
        this.playlistDAO = playlistDAO;
        this.playlistScanTrackDAO = playlistScanTrackDAO;
        this.userDAO = userDAO;
    }

    @Override
    public void prepareAddStatement(PreparedStatement unPreparedStatement, PlaylistScan playlistScan) throws SQLException {
        unPreparedStatement.setString(1, playlistScan.getId());
        unPreparedStatement.setString(2, playlistScan.getPlaylist().id());
        unPreparedStatement.setString(3, playlistScan.getRequestedByUser().id());
        unPreparedStatement.setBoolean(4, playlistScan.isExportComplete());
        unPreparedStatement.setString(5, getExtendsPlaylistScanId(playlistScan));
        unPreparedStatement.setTimestamp(6, playlistScan.getTimestamp());
    }

    @Override
    public void prepareUpdateStatement(PreparedStatement unPreparedStatement, PlaylistScan playlistScan) throws SQLException {
        unPreparedStatement.setString(1, playlistScan.getPlaylist().id());
        unPreparedStatement.setString(2, playlistScan.getRequestedByUser().id());
        unPreparedStatement.setBoolean(3, playlistScan.isExportComplete());
        unPreparedStatement.setString(4, getExtendsPlaylistScanId(playlistScan));
        unPreparedStatement.setString(5, playlistScan.getId());
    }

    @Override
    public PlaylistScan buildFromTableWildcardQuery(ResultSet resultSet) throws SQLException {
        String extendsPlaylistScanId = resultSet.getString("id");

        return new PlaylistScan(
                resultSet.getString("id"),
                playlistDAO.get(resultSet.getString("playlist_id")),
                playlistScanTrackDAO.getFromPlaylistScan(resultSet.getString("id")),
                userDAO.get(resultSet.getString("requested_by_user_id")),
                resultSet.getBoolean("export_completed"),
                extendsPlaylistScanId == null ? null : playlistScanDAO.get(extendsPlaylistScanId), // TODO check if works
                resultSet.getTimestamp("track_added_at")
        );
    }

    @Override
    public String getPrimaryKey(PlaylistScan playlistScan) {
        return playlistScan.getId();
    }

    private String getExtendsPlaylistScanId(PlaylistScan playlistScan){
        PlaylistScan extendsPlaylistScan = playlistScan.getExtendsPlaylistScan();
        return extendsPlaylistScan == null ? null : extendsPlaylistScan.getId();
    }
}
