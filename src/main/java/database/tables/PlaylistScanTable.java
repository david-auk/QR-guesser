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
                "",
                ""
        );
        this.playlistScanDAO = playlistScanDAO;
        this.playlistDAO = playlistDAO;
        this.playlistScanTrackDAO = playlistScanTrackDAO;
        this.userDAO = userDAO;
    }

    @Override
    public void prepareAddStatement(PreparedStatement unPreparedStatement, PlaylistScan playlistScan) throws SQLException {

    }

    @Override
    public void prepareUpdateStatement(PreparedStatement unPreparedStatement, PlaylistScan playlistScan) throws SQLException {

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
}
