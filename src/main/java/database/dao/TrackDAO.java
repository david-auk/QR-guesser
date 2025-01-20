package database.dao;

import database.core.GenericDAO;
import database.tables.TrackTable;
import spotify.Artist;
import spotify.PlaylistScanTrack;
import spotify.Track;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TrackDAO extends GenericDAO<Track, String> {

    private final AlbumDAO albumDAO;

    public TrackDAO(AlbumDAO albumDAO) {
        super(new TrackTable());
        this.albumDAO = albumDAO;
    }

    private void addArtistToTrack(Artist artist, Track track) {
        String query = "INSERT INTO artist_track (artist_id, track_id) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, artist.id());
            preparedStatement.setString(2, track.id());
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(Track track) {
        // Add the album if it does not already exist
        if (!albumDAO.exists(track.album())){
            albumDAO.add(track.album());
        }

        // Add the actual track
        if (!exists(track)){
            super.add(track);

            // Add the associated artists to the database
            for (Artist artist : track.artists()) {
                addArtistToTrack(artist, track);
            }
        }
    }
}
