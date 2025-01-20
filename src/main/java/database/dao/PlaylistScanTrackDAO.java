package database.dao;

import database.core.GenericDAO;
import database.tables.PlaylistScanTrackTable;
import spotify.PlaylistScanTrack;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PlaylistScanTrackDAO extends GenericDAO<PlaylistScanTrack, String> {

    public PlaylistScanTrackDAO(PlaylistScanDAO playlistScanDAO, TrackDAO trackDAO) {
        super(new PlaylistScanTrackTable(playlistScanDAO, trackDAO));
    }

    public List<PlaylistScanTrack> getFromPlaylistScan(String playlistId) {
        String query = "SELECT * FROM playlist_scan_track WHERE playlist_scan_id = ?";
        List<PlaylistScanTrack> playlistScanTracks;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1, playlistId);
            playlistScanTracks = getEntities(preparedStatement.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return playlistScanTracks;
    }
}
