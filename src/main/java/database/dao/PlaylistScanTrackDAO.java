package database.dao;

import database.core.TimestampedDAO;
import database.tables.PlaylistScanTrackTable;
import spotify.PlaylistScanTrack;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PlaylistScanTrackDAO extends TimestampedDAO<PlaylistScanTrack, String> {

    private final TrackDAO trackDAO;

    public PlaylistScanTrackDAO(PlaylistScanDAO playlistScanDAO, TrackDAO trackDAO) {
        super(new PlaylistScanTrackTable(playlistScanDAO, trackDAO));
        this.trackDAO = trackDAO;
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

    @Override
    public void add(PlaylistScanTrack playlistScanTrack) {

        // Add the track
        trackDAO.add(playlistScanTrack.track());

        // Add the actual playlistScanTrack
        super.add(playlistScanTrack);
    }
}
